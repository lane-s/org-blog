(ns ls-portfolio-blog.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [ls-portfolio-blog.handler :refer :all]
            [ls-portfolio-blog.db :refer [db]]
            [ls-portfolio-blog.db-test :refer [db-fixture test-db]]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(use-fixtures :once db-fixture)

(defn add-post-request [post]
  (app (-> (mock/request :post "/api/post")
           (mock/json-body post))))

(deftest test-api
  (with-redefs [db test-db]
    (testing "Add posts"
      (let [res (do (add-post-request {:filename "test.org"
                                       :post "test body"})
                    (add-post-request {:filename "test2.org"
                                       :post "test body 2"}))]
        (is (= (:status res) 200))
        (is (= (:body res) "1"))))
    (testing "List posts"
      (let [res (app (mock/request :get "/api/posts"))]
        (is (= (:status res) 200))
        (is (= (-> res :body (json/parse-string true))
               [{:id 1
                 :filename "test.org"
                 :post "test body"}
                {:id 2
                 :filename "test2.org"
                 :post "test body 2"}]))))
    (testing "Get by filename"
      (let [res (app (mock/request :get "/api/post/test2.org"))]
        (is (= (:status res) 200))
        (is (= (-> res :body (json/parse-string true) :post)
               "test body 2"))))))
