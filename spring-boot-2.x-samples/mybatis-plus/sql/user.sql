-- auto-generated definition
create table user
(
    id    bigint      not null comment '主键ID'
        primary key,
    name  varchar(30) null comment '姓名',
    age   int         null comment '年龄',
    email varchar(50) null comment '邮箱'
);

INSERT INTO user (id, name, age, email) VALUES (1, 'Jone', 18, 'test1@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (2, 'Jack', 20, 'test2@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (3, 'Tom', 28, 'test3@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (4, 'Sandy', 21, 'test4@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (5, 'Billie', 24, 'test5@baomidou.com');