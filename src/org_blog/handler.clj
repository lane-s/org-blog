(ns org-blog.handler
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.spec :as spec]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [muuntaja.core :as m]
            [org-parser.core :as org-parser]
            [org-blog.db :refer [db]]
            [org-blog.db.posts :as posts]))

(def app
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "my-api"
                              :description "with reitit-ring"}}
             :handler (swagger/create-swagger-handler)}}]
     ["/posts" {:swagger {:tags ["posts"]}
                :get {:summary "Get a list of all posts in the database"
                      :handler (fn [_]
                                 {:status 200
                                  :body (posts/get-all db)})}}]
     ["/post"
      {:swagger {:tags ["posts"]}
       :post {:summary "Preview or upload a new post"
              :parameters {:body {:filename string?
                                  :path_relative_to_home string?
                                  :post string?
                                  :preview string?}}
              :handler (fn [{{body :body} :parameters}]
                         (if (#{"true"} (:preview body))
                           (println "This should send a preview event")
                           (do (posts/add-or-update db (select-keys body [:post
                                                                          :filename
                                                                          :path_relative_to_home]))
                               {:status 200})))}}]
     ["/post/:filename" {:swagger {:tags ["posts"]}
                         :get {:summary "Get a post by filename"
                               :parameters {:path {:filename string?}
                                            :query {:raw int?}}
                               :handler (fn [{{{:keys [raw]} :query
                                               {:keys [filename]} :path} :parameters}]
                                          (let [result (posts/get-by-filename db {:filename filename})]
                                            {:status 200
                                             :body (if (= raw 1)
                                                     result
                                                     (let [parsed-post (org-parser/parse (:post result))]
                                                       (assoc result :post parsed-post)))}))}
                         :delete {:summary "Remove a post"
                                  :parameters {:path {:filename string?}}
                                  :handler (fn [{{{:keys [filename]} :path} :parameters}]
                                             (do (posts/remove-by-filename db {:filename filename})
                                                 {:status 200}))}}]]
    {:exception pretty/exception
     :data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [swagger/swagger-feature
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"
      :config {:validatorUrl nil
               :operationsSorter "alpha"}})
    (constantly {:status 404}))))
