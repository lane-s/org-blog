(ns org-blog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults api-defaults]]
            [org-blog.db :refer [db]]
            [org-blog.db.posts :as posts]
            [org-blog.helper.routes :refer [defroutes-api]]
            [org-parser.core :as org-parser]
            [cheshire.core :as json]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(defn add-or-preview-post [body]
  (let [{:keys [filename post preview]} body
        post-json (-> post org-parser/parse json/generate-string)]
    (if (#{"true"} preview)
      (println "This should send a preview event")
      (posts/insert db {:filename filename
                        :post post-json}))))

(defroutes-api api-routes
  (GET "posts" [] (posts/get-all db))
  (POST "post" body (add-or-preview-post body))
  (DELETE "post/:filename"
          [filename]
          (posts/remove-by-filename db {:filename filename}) )
  (GET "post/:filename"
    [filename]
    (posts/get-by-filename db {:filename filename})))

(def app
  (routes (wrap-defaults api-routes api-defaults)
          (wrap-defaults app-routes site-defaults)))
