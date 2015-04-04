# --- Post Schema

# --- !Ups

create sequence post_id_seq;

create table posts(
    id      bigint      not null primary key default nextval('post_id_seq'),
    title   text        not null,
    body    text        not null
);

# --- !Downs

drop table Posts;
drop sequence post_id_seq;