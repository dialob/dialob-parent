alter table questionnaire 
	add column `tenant_id` char(36) not null default ''
	first;

alter table form_document 
	add column `tenant_id` char(36) not null default ''
	first;

alter table form 
	add column `tenant_id` char(36) not null default ''
	first;

alter table form_rev 
	add column `tenant_id` char(36) not null default ''
	first;

alter table `form_rev` drop primary key, add primary key (`tenant_id`, `form_name`, `name`);
alter table `form` drop primary key, add primary key (tenant_id, name);
alter table `form_rev` add foreign key (`tenant_id`, `form_name`) references form(`tenant_id`, `name`);

create index `form_document_tenant_id` on `form_document`(`tenant_id`);
create index `questionnaire_tenant_id` on `questionnaire`(`tenant_id`);
