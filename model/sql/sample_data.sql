
insert into APPUSER(FIRST_NAME,LAST_NAME,MIDDLE_NAME,LOGIN_ID,EMAIL,PASSWORD_HASH)
values ('John', 'John', 'TM11', 'John','John@test.com','111111');

insert into ROLE(NAME, DESCR) values ('ADMIN', 'Administrator');
insert into ROLE(NAME, DESCR) values ('APP_USER', 'Application User');

insert into PERMISSION(NAME, DESCR) values ('EDIT_ALL', 'Edit All');
insert into PERMISSION(NAME, DESCR) values ('VIEW_ALL', 'View All');
insert into PERMISSION(NAME, DESCR) values ('EDIT', 'Edit');
insert into PERMISSION(NAME, DESCR) values ('VIEW', 'View');

insert into ROLE_PERMISSION_MAP(ROLE_ID, PERMISSION_ID)
values ((select ID from ROLE where name = 'ADMIN'), (select ID from PERMISSION where name = 'EDIT_ALL'));
insert into ROLE_PERMISSION_MAP(ROLE_ID, PERMISSION_ID)
values ((select ID from ROLE where name = 'ADMIN'), (select ID from PERMISSION where name = 'VIEW_ALL'));
insert into ROLE_PERMISSION_MAP(ROLE_ID, PERMISSION_ID)
values ((select ID from ROLE where name = 'APP_USER'), (select ID from PERMISSION where name = 'EDIT'));
insert into ROLE_PERMISSION_MAP(ROLE_ID, PERMISSION_ID)
values ((select ID from ROLE where name = 'APP_USER'), (select ID from PERMISSION where name = 'VIEW'));

insert into APPUSER_ROLE_MAP(USER_ID, ROLE_ID) values ((select ID from APPUSER where LOGIN_ID = 'John'), (select ID from ROLE where NAME = 'ADMIN'));


