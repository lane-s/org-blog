(ns org-blog.db-test
  (:require [migratus.core :as migratus]
            [org-blog.db :refer :all]))

(def test-db (assoc db :subname "//localhost:5432/portfolio_blog_test"))
(def config {:store :database
             :migration-dir "migrations"
             :db test-db})

(defn db-fixture [f]
  (migratus/reset config)
  (f))
