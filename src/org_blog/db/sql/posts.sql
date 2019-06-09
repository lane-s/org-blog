-- :name create-table
-- :command :execute
-- :result :raw
-- :doc Create posts table
create table posts (
  id serial primary key,
  filename varchar unique not null,
  path_relative_to_home varchar,
  post varchar not null,
  created_at timestamp not null,
  updated_at timestamp not null
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
insert into posts (filename, path_relative_to_home, post, created_at, updated_at)
values (:filename, :path_relative_to_home, :post, current_timestamp, current_timestamp);

-- :name update-by-filename :! :n
-- :doc Update a post by filename
update posts set
post = :post,
updated_at = current_timestamp
--~ (when (contains? params :path_relative_to_home) ", path_relative_to_home = :path_relative_to_home")
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
select id, filename, path_relative_to_home, created_at, updated_at from posts;
