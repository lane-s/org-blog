(ns ls-portfolio-blog.db.posts
  (:require [ls-portfolio-blog.db :refer [def-db-fns-with-spec db]]))

(def-db-fns-with-spec db "ls_portfolio_blog/db/sql/posts.sql")
