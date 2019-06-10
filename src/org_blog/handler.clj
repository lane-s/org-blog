(ns org-blog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults api-defaults]]
            [org-parser.core :as org-parser]
            [org-blog.db :refer [db]]
            [org-blog.db.posts :as posts]
            [org-blog.helper.routes :refer [defroutes-api]]
            [cheshire.core :as json]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(defn handle-post
  "Handle POST request for adding,
  updating, and previewing posts"
  [body]
  (let [{:keys [preview]} body]
    (if (#{"true"} preview)
      (println "This should send a preview event")
      (posts/add-or-update db (select-keys body [:post
                                                 :filename
                                                 :path_relative_to_home])))))

(defroutes-api api-routes
  (GET "posts" [] (posts/get-all db))
  (POST "post" body (handle-post body))
  (GET "post/:filename"
    [filename raw]
    (let [result (posts/get-by-filename db {:filename filename})]
      (if raw
        result
        (assoc result :post (org-parser/parse (:post result))))))
  (DELETE "post/:filename"
          [filename]
          (posts/remove-by-filename db {:filename filename})))

(def app
  (routes (wrap-defaults api-routes api-defaults)
          (wrap-defaults app-routes site-defaults)))
