(ns org-blog.db.posts
  (:require [hugsql.core :as hugsql]
            [org-blog.db :refer [defmigration]]))

(hugsql/def-db-fns  "org_blog/db/sql/posts.sql")

(defmigration add-posts create-table remove-table)
