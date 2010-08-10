alter table `EXECUTED_ACTION` add `SUBJECT` text, add `OID_ERASMUS_CANDIDACY_PROCESS` bigint unsigned, add `BEGIN_INTERVAL_OF_ACCEPTED_STUDENTS` timestamp NULL default NULL, add `BODY` text, add `END_INTERVAL_OF_ACCEPTED_STUDENTS` timestamp NULL default NULL, add index (OID_ERASMUS_CANDIDACY_PROCESS);
create table `ERASMUS_CANDIDACY_EXECUTED_ACTION_ERASMUS_INDIVIDUAL_PROCESS` (`OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS` bigint unsigned, `OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION` bigint unsigned, primary key (OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS, OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION), index (OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS), index (OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION)) type=InnoDB, character set latin1;create table `ERASMUS_CANDIDACY_EXECUTED_ACTION_ERASMUS_INDIVIDUAL_PROCESS` (`OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS` bigint unsigned, `OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION` bigint unsigned, primary key (OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS, OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION), index (OID_ERASMUS_INDIVIDUAL_CANDIDACY_PROCESS), index (OID_ERASMUS_CANDIDACY_PROCESS_EXECUTED_ACTION)) type=InnoDB, character set latin1;
