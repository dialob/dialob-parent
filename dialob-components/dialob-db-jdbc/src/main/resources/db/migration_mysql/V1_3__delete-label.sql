create table `form_archive` (
  `deleted` TIMESTAMP(6) not null default CURRENT_TIMESTAMP(6),
  `tenant_id` char(36) not null,
  `name` varchar(32) not null,
  `created` timestamp not null default current_timestamp,
  `updated` timestamp not null default current_timestamp,
  `latest_form_id` binary(16) not null references `form_document`(`id`) on delete cascade,
  `label` varchar(255) default '',
  primary key(`deleted`, `tenant_id`, `name`),
  index `idx_form_rev_archive` (`deleted`, `tenant_id`, `name`)
) engine=InnoDB default charset=utf8;
  
create table `form_rev_archive` (
  `deleted` TIMESTAMP(6) not null default CURRENT_TIMESTAMP(6),
  `tenant_id` char(36) not null,
  `form_name` varchar(32) not null,
  `name` varchar(255) not null default '',
  `created` timestamp not null default current_timestamp,
  `updated` timestamp not null default current_timestamp,
  `form_document_id` binary(16) not null references `form_document`(`id`) on delete restrict,
  primary key (`deleted`, `tenant_id`, `form_name`, `name`),
  index `idx_form_rev_archive` (`deleted`, `tenant_id`, `form_name`, `name`)
) engine=InnoDB default charset=utf8;

DELIMITER $$
CREATE TRIGGER `archive_form_before_delete`
BEFORE DELETE
   ON `form` FOR EACH ROW
BEGIN
   DECLARE now_ts TIMESTAMP(6);
   SET now_ts = CURRENT_TIMESTAMP(6);
   INSERT INTO `form_archive` (`deleted`, `tenant_id`, `name`, `created`, `updated`, `latest_form_id`, `label`) 
   	 VALUES (now_ts, OLD.`tenant_id`, OLD.`name`, OLD.`created`, OLD.`updated`, OLD.`latest_form_id`, OLD.`label`);
   INSERT INTO `form_rev_archive` (`deleted`, `tenant_id`, `form_name`, `name`, `created`, `updated`, `form_document_id`) 
   	 SELECT now_ts, `tenant_id`, `form_name`, `name`, `created`, `updated`, `form_document_id` FROM `form_rev` WHERE `form_name` = OLD.`name`;
   DELETE FROM `form_rev` WHERE `form_name` = OLD.`name`; 
END; $$
DELIMITER ;
