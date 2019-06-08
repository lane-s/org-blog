(ns ls-portfolio-blog.helper.routes
  (:require [compojure.core :refer :all]
            [ring.util.request :refer [body-string]]
            [ls-portfolio-blog.helper.coll :refer [replace-last]]
            [cheshire.core :as json]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string data)})

(defn- bind-body
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
