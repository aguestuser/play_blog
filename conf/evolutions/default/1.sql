# --- Post Schema

# --- !Ups

create sequence post_id_seq;

create table Posts(
    id      bigint      not null primary key default nextval('post_id_seq'),
    title   text        not null,
    body    text        not null
);

# --- !Downs

drop sequence post_id_seq;
drop table Posts;