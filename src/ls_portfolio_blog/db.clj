(ns ls-portfolio-blog.db
  (:require [hugsql.core :as hugsql]))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/portfolio_blog"
   :user "postgres"
   :password "docker"})

(defmacro def-db-fns-with-spec
  "Defines HugSql db functions for the given file
  with the db argument applied"
  [db file]
  `(doseq [[name# v#] (hugsql/map-of-db-fns ~file)]
     (intern *ns*
             (symbol name#)
             (partial (vary-meta (:fn v#) merge (:meta v#)) ~db))))
