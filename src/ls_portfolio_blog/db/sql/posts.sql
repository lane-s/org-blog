-- :name create-table
-- :command :execute
-- :result :raw
-- :doc Create posts table
create table posts (
  id serial primary key,
  filename varchar not null,
  serialized_post varchar not null
);

create index idx_filename on posts(filename);

-- :name remove-table
-- :command :execute
-- :result :raw
-- :doc Remove posts table
drop index idx_filename;
drop table posts;

-- :name insert :! :n
-- :doc Insert a single post returning affected row count
insert into posts (filename, serialized_post)
values (:filename, :serialized_post);

-- :name get-by-filename :? :1
-- :doc Get post by filename
select * from posts
where filename = :filename;

-- :name get-all :? :*
-- :doc Return all posts
select * from posts;
