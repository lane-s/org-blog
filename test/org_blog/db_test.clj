(ns org-blog.db-test
  (:require [migratus.core :as migratus]
            [org-blog.db :refer :all]))

(def test-db (assoc db :subname "//localhost/org_blog_test"))
(def config (assoc migratus-config :db test-db))

(defn db-fixture [f]
  (migratus/reset config)
  (f))
