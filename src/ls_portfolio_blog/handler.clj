(ns ls-portfolio-blog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults api-defaults]]
            [ls-portfolio-blog.db :refer [db]]
            [ls-portfolio-blog.db.posts :as posts]
            [ls-portfolio-blog.helper.routes :refer [defroutes-api]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(defroutes-api api-routes
  (GET "posts" [] (posts/get-all db))
  (POST "post" body (posts/insert db body))
  (GET "post/:filename"
    [filename]
    (posts/get-by-filename db {:filename filename})))

(def app
  (routes (wrap-defaults api-routes api-defaults)
          (wrap-defaults app-routes site-defaults)))
