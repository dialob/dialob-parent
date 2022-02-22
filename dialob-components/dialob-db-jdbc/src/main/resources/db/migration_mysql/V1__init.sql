create table `form_document` (
  `id` binary(16) not null,
  `rev` integer not null,
  `created` timestamp not null default current_timestamp,
  `updated` timestamp not null default current_timestamp,
  `data` mediumblob,
   primary key (`id`),
   index `idx_form_rev` (`rev`)
 ) engine=InnoDB default charset=utf8;

create table questionnaire (
	 `id` binary(16) not null,
	 `rev` integer not null,
	 `created` timestamp not null default current_timestamp,
	 `updated` timestamp not null default current_timestamp,
	 `data` mediumblob,
	 primary key (`id`),
	 index `idx_questionnaire_rev` (`rev`)
 ) engine=InnoDB default charset=utf8;
