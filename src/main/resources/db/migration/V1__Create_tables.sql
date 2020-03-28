create table LINK_TO_BE_PROCESSED(
    LINK VARCHAR(1000)
);

create table LINK_ALREADY_PROCESSED(
    LINK VARCHAR(1000)
);

create table SINANEWS(
    id bigint auto_increment primary key ,
    title text,
    content text,
    url varchar(1000),
    create_at timestamp default now(),
    modify_at timestamp default now()
) DEFAULT CHARSET=utf8mb4 ;