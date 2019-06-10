(ns org-blog.db-test
  (:require [migratus.core :as migratus]
            [org-blog.db :refer :all]))

(def config (assoc-in migratus-config [:db :subname] "//0.0.0.0/org_blog_test"))

(defn db-fixture [f]
  (migratus/reset migratus-config)
  (f))
