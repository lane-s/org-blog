(defproject org-blog "0.1.0-SNAPSHOT"
  :description "Backend for storing and parsing .org blog posts"
  :url "https://github.com/lane-s/org-blog"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.3.7"]
                 [metosin/muuntaja "0.6.4"]
                 [com.layerware/hugsql "0.4.9"]
                 [org.postgresql/postgresql "42.2.2"]
                 [migratus "1.2.3"]
                 [org.slf4j/slf4j-log4j12 "1.7.26"]
                 [cheshire "5.8.1"]]
  :plugins [[lein-ring "0.12.5"]
            [migratus-lein "0.7.2"]]
  :ring {:handler org-blog.handler/app}
  :migratus {:store :database
             :init-script "init.sql"
             :init-in-transaction? false
             :db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname "//0.0.0.0:5432/org_blog"
                  :user "postgres"
                  :password "docker"}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :uberjar {:aot :all
             :main org-blog.main}})
