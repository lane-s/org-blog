(ns ls-portfolio-blog.db.posts
  (:require [hugsql.core :as hugsql]
            [ls-portfolio-blog.db :refer [defmigration]]))

(hugsql/def-db-fns  "ls_portfolio_blog/db/sql/posts.sql")

(defmigration add-posts create-table remove-table)
