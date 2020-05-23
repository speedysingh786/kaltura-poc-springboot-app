
create table APPUSER (
    ID                  int not null generated always as identity,
    FIRST_NAME          varchar(100),
    LAST_NAME           varchar(100),
    MIDDLE_NAME         varchar(100),
    LOGIN_ID            varchar(100),
    EMAIL               varchar(100),
    PASSWORD_HASH       varchar(1000),

    primary key(ID),
    unique (LOGIN_ID)
);

create table ROLE (
    ID          int not null generated always as identity,
    NAME        varchar(100),
    DESCR        varchar(1000),

    primary key(ID),
    unique (NAME)
);

create table PERMISSION (
    ID          int not null generated always as identity,
    NAME        varchar(100),
    DESCR        varchar(1000),

    primary key(ID),
    unique (NAME)
);

create table ROLE_PERMISSION_MAP (
    ROLE_ID          int,
    PERMISSION_ID    int,

    unique (ROLE_ID, PERMISSION_ID),
    constraint FK_ROLE_PERMISSION_MAP_ROLE_ID foreign key (ROLE_ID) references ROLE (ID),
    constraint FK_ROLE_PERMISSION_MAP_PERMISSION_ID foreign key (PERMISSION_ID) references PERMISSION (ID)
);

create table APPUSER_ROLE_MAP (
    USER_ID          int,
    ROLE_ID          int,

    unique (USER_ID, ROLE_ID),
    constraint FK_APPUSER_ROLE_MAP_USER_ID foreign key (USER_ID) references APPUSER (ID),
    constraint FK_APPUSER_ROLE_MAP_ROLE_ID foreign key (ROLE_ID) references ROLE (ID)
);

create table ARTEFACT (
    ID                  int,
    NAME                varchar(400),
    URLKEY              varchar(4000),
    TYPE                varchar(10),
    SIZEINBYTES         int,
    PARENT_ARTEFACT_ID  int,

    primary key(ID),
    constraint FK_ARTEFACT_PARENT_ARTEFACT_ID foreign key (PARENT_ARTEFACT_ID) references ARTEFACT (ID)
);

create table ARTEFACT_STATUS (
    ARTEFACT_ID             int,
    TRANSCODE_COMPLETE      varchar(1),

    primary key(ARTEFACT_ID),
    constraint FK_ARTEFACT_STATUS_ARTEFACT_ID foreign key (ARTEFACT_ID) references ARTEFACT (ID)
);

create sequence SEQ_ARTEFACT_ID start with 1;
