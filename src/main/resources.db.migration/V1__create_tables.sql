create table SINANEWS (
    id bigint auto_increment primary key,
    title text,
    content text,
    url varchar (10000),
    create_at timestamp ,
    modify_at timestamp
);

create table LINK_TO_BE_PROCESSED (
    link varchar (10000)
);

create table LINK_ALREADY_PROCESSED (
    link varchar (10000)
);