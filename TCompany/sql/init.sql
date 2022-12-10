CREATE DATABASE IF NOT EXISTS tcdb;
CREATE USER IF NOT EXISTS 'user'@'%' IDENTIFIED BY 'password';
GRANT SELECT,UPDATE,INSERT ON tcdb.* TO 'user'@'%';
FLUSH PRIVILEGES;

USE tcdb;
create table users (
    id int not null auto_increment, primary key (id),
    username varchar(64) not null,
    email varchar(64) not null,
    firstname varchar(64) not null,
    lastname varchar(64) not null,
    password varchar(64) not null
);
create table roles (
    id int not null auto_increment, primary key (id),
    name varchar(64) not null
);
create table tours (
    id int not null auto_increment, primary key (id),
    start varchar(64) not null,
    finish varchar(64) not null,
    price integer not null,
    date varchar(64) not null,
    count integer null
);
create table descriptions (
    id int not null auto_increment, primary key (id),
    img varchar(128) not null,
    text varchar(128) not null,
    tour_id int not null
);
create table users_roles (
    user_id  int not null,
    roles_id int not null,
    primary key (user_id, roles_id)
);
