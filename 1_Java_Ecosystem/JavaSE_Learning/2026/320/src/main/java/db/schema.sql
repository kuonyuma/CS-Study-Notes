

create database if not exists library_db default character set utf8mb4;
use library_db;

create table if not exists books (
    id int primary key auto_increment,
    name varchar(100) not null unique,
    author varchar(100) not null,
    type varchar(50) not null,
    price int not null,
    count int not null default 1,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);


