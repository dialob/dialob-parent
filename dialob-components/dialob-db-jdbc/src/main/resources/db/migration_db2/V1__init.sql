create table form_document (
  tenant_id char(36) not null,
  id char(16) for bit data not null,
  rev integer not null,
  created timestamp not null,
  updated timestamp not null,
  data blob(16M),
  primary key (tenant_id, id)
);

create table questionnaire (
  tenant_id char(36) not null,
  id char(16) for bit data not null, 
  rev integer not null, 
  status char(10) default null,
  form_document_id char(16) for bit data not null,
  owner varchar(64) default null,
  created timestamp not null, 
  updated timestamp not null, 
  data blob(16M),
  primary key(tenant_id, id),
  foreign key(tenant_id, form_document_id) references form_document(tenant_id, id) on update restrict on delete restrict
);

create table form (
  tenant_id char(36) not null,
  name varchar(128) not null,
  description varchar(255) default null,
  created timestamp not null default current timestamp,
  updated timestamp not null default current timestamp,
  latest_form_id char(16) for bit data not null,
  label varchar(255) default '',
  primary key(tenant_id, name),
  foreign key(tenant_id, latest_form_id) references form_document(tenant_id, id) on update restrict on delete restrict
);

create table form_archive (
  deleted timestamp not null default current timestamp,
  tenant_id char(36) not null,
  name varchar(128) not null,
  description varchar(255) default null,
  created timestamp not null,
  updated timestamp not null,
  latest_form_id char(16) for bit data not null,
  label varchar(255) default '',
  primary key(updated, tenant_id, name),
  foreign key(tenant_id, latest_form_id) references form_document(tenant_id, id) on update restrict on delete restrict
);
  

create table form_rev (
  tenant_id char(36) not null,
  form_name varchar(128) not null,
  name varchar(255) not null default '',
  description varchar(255) default null,
  type char(7) default 'NORMAL',
  ref_name varchar(128) default null,
  created timestamp not null default current timestamp,
  updated timestamp not null default current timestamp,
  form_document_id char(16) for bit data not null,
  primary key (tenant_id, form_name, name),
--  foreign key(tenant_id, form_name) references form(tenant_id, name) ON DELETE NO ACTION,
  foreign key(tenant_id, form_document_id) references form_document(tenant_id, id) on update restrict on delete restrict
);

create index form_rev_form_document_id on form_rev(tenant_id, form_document_id);

  
create table form_rev_archive (
  deleted timestamp not null default current timestamp,
  tenant_id char(36) not null,
  form_name varchar(128) not null,
  name varchar(255) not null default '',
  description varchar(255) default null,
  type char(7) default 'NORMAL',
  ref_name varchar(128) default null,
  created timestamp not null,
  updated timestamp not null,
  form_document_id char(16) for bit data not null,
  primary key (updated, tenant_id, form_name, name),
  foreign key(tenant_id, form_document_id) references form_document(tenant_id, id) on update restrict on delete restrict
);

CREATE OR REPLACE VIEW form_id_to_name AS
	SELECT tenant_id,   latest_form_id as id,              name, null as label, created, updated, CAST(NULL as timestamp) as deleted from form
	UNION ALL
	SELECT tenant_id, form_document_id as id, form_name as name, name as label, created, updated, CAST(NULL as timestamp) as deleted from form_rev
	UNION ALL
	SELECT tenant_id,   latest_form_id as id,              name, null as label, created, updated,                            deleted from form_archive
	UNION ALL
	SELECT tenant_id, form_document_id as id, form_name as name, name as label, created, updated,                            deleted from form_rev_archive
;


CREATE TRIGGER archive_form_before_delete
 AFTER DELETE ON form
 REFERENCING OLD AS OLD
 FOR EACH ROW
 BEGIN ATOMIC
   DECLARE now_ts timestamp default current timestamp;
   INSERT INTO form_archive (deleted, tenant_id, name, created, updated, latest_form_id, label) values (now_ts, OLD.tenant_id, OLD.name, OLD.created, OLD.updated, OLD.latest_form_id, OLD.label);
   DELETE FROM form_rev WHERE tenant_id = OLD.tenant_id and form_name = OLD.name; 
 END;

CREATE TRIGGER archive_form_rev_on_update
  AFTER UPDATE ON form_rev 
  REFERENCING OLD AS OLD
  FOR EACH ROW
BEGIN ATOMIC
   INSERT INTO form_rev_archive (tenant_id, form_name, name, created, updated, form_document_id, type, ref_name) values (OLD.tenant_id, OLD.form_name, OLD.name, OLD.created, OLD.updated, OLD.form_document_id, OLD.type, OLD.ref_name); 
END;

CREATE TRIGGER archive_form_rev_on_delete
  AFTER DELETE ON form_rev
  REFERENCING OLD AS OLD
  FOR EACH ROW
BEGIN ATOMIC
   INSERT INTO form_rev_archive (deleted,tenant_id, form_name, name, created, updated, form_document_id, type, ref_name) values (CURRENT TIMESTAMP, OLD.tenant_id, OLD.form_name, OLD.name, OLD.created, OLD.updated, OLD.form_document_id, OLD.type, OLD.ref_name); 
END;


