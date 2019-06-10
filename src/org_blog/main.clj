(ns org-blog.main
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [migratus.core :as migratus]
            [clojure.java.shell :refer [sh]]
            [org-blog.db :refer [migratus-config db-host]]
            [org-blog.handler :refer [app]])
  (:gen-class))

(import org.postgresql.util.PSQLException)
(import java.lang.ClassCastException)

(defn -init-db
  "Repeatedly try to initialize the database"
  []
  (try
    (migratus/init (assoc-in migratus-config [:db :subname] (str "//" db-host "/postgres")))
    (catch Exception e (Thread/sleep 1000) (-init-db))))

(defn -main
  [& args]
  (-init-db)
  (migratus/migrate migratus-config)
  (run-jetty app {:port 3000}))
