create table LINK_TO_BE_PROCESSED(
    LINK VARCHAR(20000)
);

create table LINK_ALREADY_PROCESSED(
    LINK VARCHAR(20000)
);

create table SINANEWS(
    id bigint auto_increment primary key ,
    title text,
    content text,
    url varchar(200000),
    create_at timestamp,
    modify_at timestamp
);