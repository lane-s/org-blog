-- :name create-table
-- :command :execute
-- :result :raw
-- :doc Create posts table
create table posts (
  id serial primary key,
  filename varchar not null,
  post varchar not null
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
insert into posts (filename, post)
values (:filename, :post);

-- :name update-by-filename :! :n
-- :doc Update a post by filename
update posts set post = :post
where filename = :filename

-- :name get-by-filename :? :1
-- :doc Get post by filename
select * from posts
where filename = :filename;

-- :name remove-by-filename :! :n
-- :doc Remove a single post returning affected row count
delete from posts
where filename = :filename;

-- :name get-all :? :*
-- :doc Return all posts
select id, filename from posts;
