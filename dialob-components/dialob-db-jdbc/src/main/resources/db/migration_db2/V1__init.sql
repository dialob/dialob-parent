create table form_document (
  id char(16) for bit data not null primary key,
  rev integer not null,
  created timestamp not null,
  updated timestamp not null,
  data blob(16M));

create table questionnaire(
	 id char(16) for bit data not null primary key, 
	 rev integer not null, 
	 created timestamp not null, 
	 updated timestamp not null, 
	 data blob(16M));
