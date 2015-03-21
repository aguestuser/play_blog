# Post Schema

# --- !Ups

create table Posts(
    id      bigserial   primary key unique,
    title   text        not null,
    body    text        not null
);

# --- !Downs

drop table Posts;