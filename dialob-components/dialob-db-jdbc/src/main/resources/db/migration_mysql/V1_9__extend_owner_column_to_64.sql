alter table `questionnaire` 
	modify `owner` varchar(64) default null
after updated;
