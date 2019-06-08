(ns org-blog.db
  (:require [hugsql.core :as hugsql]))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/portfolio_blog"
   :user "postgres"
   :password "docker"})

(defmacro defmigration [name up down]
  `(do
     (defn ~(symbol (str name "-up"))
       [config#]
       (~up (:db config#)))
     (defn ~(symbol (str name "-down"))
       [config#]
       (~down (:db config#)))))
