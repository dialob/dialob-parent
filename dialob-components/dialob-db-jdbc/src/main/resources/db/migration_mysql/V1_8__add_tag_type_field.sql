alter table form_rev 
	add column `type` char(7) default 'NORMAL';
alter table form_rev 
	add column `ref_name` varchar(128) default null;
  
alter table `form_rev` 
	add constraint `form_rev_ref_name_fkey` foreign key(`tenant_id`, `form_name`, `ref_name`) references `form_rev`(`tenant_id`, `form_name`, `name`) on update restrict on delete restrict;

alter table form_rev_archive 
	add column `type` char(7) default 'NORMAL';
alter table form_rev_archive 
	add column `ref_name` varchar(128) default null;

DROP TRIGGER `archive_form_before_delete`;
DELIMITER $$
CREATE TRIGGER `archive_form_before_delete`
BEFORE DELETE
   ON `form` FOR EACH ROW
BEGIN
   DECLARE now_ts TIMESTAMP(6);
   SET now_ts = CURRENT_TIMESTAMP(6);
   INSERT INTO `form_archive` (`deleted`, `tenant_id`, `name`, `created`, `updated`, `latest_form_id`, `label`) 
   	 VALUES (now_ts, OLD.`tenant_id`, OLD.`name`, OLD.`created`, OLD.`updated`, OLD.`latest_form_id`, OLD.`label`);
   DELETE FROM `form_rev` WHERE `form_name` = OLD.`name`; 
END; $$

CREATE TRIGGER `archive_form_rev_on_update`
BEFORE UPDATE
   ON `form_rev` FOR EACH ROW
BEGIN
   INSERT INTO `form_rev_archive` (`tenant_id`, `form_name`, `name`, `created`, `updated`, `form_document_id`, `type`, `ref_name`) values (OLD.`tenant_id`, OLD.`form_name`, OLD.`name`, OLD.`created`, OLD.`updated`, OLD.`form_document_id`, OLD.`type`, OLD.`ref_name`); 
END; $$

CREATE TRIGGER `archive_form_rev_on_delete`
BEFORE DELETE
   ON `form_rev` FOR EACH ROW
BEGIN
   INSERT INTO `form_rev_archive` (`deleted`,`tenant_id`, `form_name`, `name`, `created`, `updated`, `form_document_id`, `type`, `ref_name`) values (CURRENT_TIMESTAMP(6), OLD.`tenant_id`, OLD.`form_name`, OLD.`name`, OLD.`created`, OLD.`updated`, OLD.`form_document_id`, OLD.`type`, OLD.`ref_name`); 
END; $$
DELIMITER ;
