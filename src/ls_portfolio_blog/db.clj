(ns ls-portfolio-blog.db
  (:require [hugsql.core :as hugsql]))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/portfolio_blog"
   :user "postgres"
   :password "docker"})
