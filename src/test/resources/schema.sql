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