create table if not exists posts(
    id bigserial primary key,
    title varchar(256) not null,
    shortDescription varchar(256) not null,
    content varchar(256) not null,
    imageUrl varchar(256),
    likes int DEFAULT 0
);

create table if not exists tags(
    id bigserial primary key,
    name varchar(256) not null unique
);

create table if not exists post_tag_links(
    post_id bigint not null,
    tag_id bigint not null
);

insert into tags(name) values ('First');
insert into tags(name) values ('Second');

insert into post_tag_links(post_id, tag_id) values(1,1);

insert into posts(title, shortDescription, content) values ('First', '123', '123456789. 12314123. 12312312. 12312.');
insert into posts(title, shortDescription, content, likes) values ('Second', '321', '321654987. 1231231.\n 513345.', 10);
insert into posts(title, shortDescription, content) values ('Third', '777', '777111999. 1928hd19. 123ninsad. aolsdnoi213.\n asldk213. asdkl12.');