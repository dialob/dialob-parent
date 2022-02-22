create table form (
  name varchar(32) not null primary key,
  created timestamp not null default current timestamp,
  updated timestamp not null default current timestamp,
  latest_form_id for bit data not null references form_document(id) on delete cascade,
  label varchar(255) default ''
);
  
create table form_rev (
  form_name varchar(32) not null references form(id) 
  name varchar(255) not null default '',
  created timestamp not null default current timestamp,
  updated timestamp not null default current timestamp,
  form_document_id for bit data not null references form_document(id) on delete restrict,
  primary key (form_name, name)
);

create index form_rev_form_document_id on form_rev(form_document_id);

alter table questionnaire 
	add column status char(10) default null,
  	add column form_document_id for bit data not null references form_document(id) on delete restrict
after rev;

