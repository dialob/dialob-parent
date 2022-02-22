SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE `form_rev`
	MODIFY `form_name` varchar(128);
ALTER TABLE `form`
	MODIFY `name` varchar(128);
ALTER TABLE `form_rev_archive`
	MODIFY `form_name` varchar(128);
ALTER TABLE `form_archive`
	MODIFY `name` varchar(128);
SET FOREIGN_KEY_CHECKS=1;

