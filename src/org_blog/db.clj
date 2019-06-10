(ns org-blog.db
  (:require [hugsql.core :as hugsql]))

(def db-host (or (System/getenv "DATABASE_HOST") "0.0.0.0:5432"))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname (str "//" db-host "/org_blog")
   :user "postgres"
   :password "docker"})

(def migratus-config {:store :database
                      :init-script "init.sql"
                      :init-in-transaction? false
                      :db db})

(defmacro defmigration [name up down]
  `(do
     (defn ~(symbol (str name "-up"))
       [config#]
       (~up (:db config#)))
     (defn ~(symbol (str name "-down"))
       [config#]
       (~down (:db config#)))))
