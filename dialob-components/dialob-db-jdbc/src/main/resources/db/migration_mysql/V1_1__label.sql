
create table `form` (
  `name` varchar(32) not null primary key,
  `created` timestamp default current_timestamp,
  `updated` timestamp default current_timestamp,
  `latest_form_id` binary(16) not null references `form_document`(`id`) on delete cascade,
  `label` varchar(255) default ''
) engine=InnoDB default charset=utf8;
  
create table `form_rev` (
  `form_name` varchar(32) not null references form(name) on delete cascade on update cascade,
  `name` varchar(255) not null default '',
  `created` timestamp not null default current_timestamp,
  `updated` timestamp not null default current_timestamp,
  `form_document_id` binary(16) not null references `form_document`(`id`) on delete restrict,
  primary key(`form_name`, `name`),
  index `form_rev_form_document_id` (`form_document_id`)
) engine=InnoDB default charset=utf8;

alter table questionnaire 
	add column `status` char(10) default null,
  	add column `form_document_id` binary(16) not null references `form_document`(`id`) on delete restrict 
after rev;
