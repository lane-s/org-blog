(ns org-blog.db.posts
  (:require [hugsql.core :as hugsql]
            [org-blog.db :refer [defmigration]]))

(hugsql/def-db-fns  "org_blog/db/sql/posts.sql")

(defmigration add-posts create-table remove-table)

(defn add-or-update
  [db params]
  (if (get-by-filename db (select-keys params [:filename]))
    (update-by-filename db params)
    (insert db params)))
