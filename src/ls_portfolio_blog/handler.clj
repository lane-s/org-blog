(ns ls-portfolio-blog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults api-defaults]]
            [ring.util.request :refer [body-string]]
            [ls-portfolio-blog.db :refer [db]]
            [ls-portfolio-blog.db.posts :as posts]
            [cheshire.core :as json]))

(defn replace-last
  "Replace the last element in `coll`
  with `elem`"
  [coll elem]
  (conj (pop coll) elem))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string data)})

(defn bind-body
  "Bind the third symbol in the route
  expression to the parsed request body instead
  of the request itself"
  [expr-vec body-sym]
  (let [req (gensym "req")
        handler-fn (last expr-vec)]
    (-> expr-vec
        (assoc 2 req)
        (replace-last `(let [~body-sym (-> ~req
                                           body-string
                                           (json/parse-string true))]
                         ~handler-fn)))))

(defmacro defroutes-api
  "Prefix url with /api/, convert response to json,
  bind POST request body to param symbol"
  [name & body]
  `(defroutes ~name
     ~@(map (fn [expr]
              (let [as-vec (vec expr)
                    is-post (= 'POST (first as-vec))
                    body-sym (get as-vec 2)
                    full-url (str "/api/" (second as-vec))
                    handler-fn (last as-vec)]
                (apply list (cond-> as-vec
                              true (assoc 1 full-url)
                              true (replace-last `(json-response ~handler-fn))
                              is-post (bind-body body-sym)))))
            body)))

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
