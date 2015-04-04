# --- Add created and modifed dates to Post Scheme

# --- !Ups

alter table posts
    add column created timestamp with time zone not null,
    add column modified timestamp with time zone;

# --- !Downs

alter table posts
    drop column created,
    drop column modified;