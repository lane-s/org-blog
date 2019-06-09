(ns org-blog.db.posts
  (:require [hugsql.core :as hugsql]
            [org-parser.core :refer [org->json]]
            [org-blog.db :refer [defmigration]]))

(hugsql/def-db-fns  "org_blog/db/sql/posts.sql")

(defmigration add-posts create-table remove-table)

(defn add-or-update
  [db filename post]
  (let [params {:filename filename
                :post (org->json post)}]
    (if (get-by-filename
         db {:filename filename})
         (update-by-filename db params)
         (insert db params))))
