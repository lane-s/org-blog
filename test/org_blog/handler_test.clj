(ns org-blog.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [org-blog.handler :refer :all]
            [org-blog.db :refer [db]]
            [org-blog.db-test :refer [db-fixture test-db]]
            [org-parser.core :refer [org->json]]))

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

(def test-post (slurp "./test/org_parser/test_post.org"))
(def test-series-1 (slurp "./test/org_parser/test_post_series_1.org"))
(def test-series-2 (slurp "./test/org_parser/test_post_series_2.org"))

(defn post-is
  [filename post]
  (let [res (app (mock/request :get "/api/post/test_post.org"))]
    (is (= (:status res) 200))
    (is (= (-> res :body (json/parse-string true) :post)
           (json/parse-string post true)))))

(deftest test-api
  (with-redefs [db test-db]
    (testing "Add posts"
      (let [res (do (add-post-request {:filename "test_post.org"
                                       :path_relative_to_home "path/test_post.org"
                                       :post test-post})
                    (add-post-request {:filename "test_post_series_2.org"
                                       :path_relative_to_home "path/test_post_series_2.org"
                                       :post test-series-2}))]
        (is (= (:status res) 200))
        (is (= (:body res) "1"))))
    (testing "List posts"
      (let [res (app (mock/request :get "/api/posts"))]
        (is (= (:status res) 200))
        (let [parsed-result (-> res :body (json/parse-string true))]
          (is (= (map #(select-keys % [:id :filename :path_relative_to_home]) parsed-result)
                 [{:id 1
                   :filename "test_post.org"
                   :path_relative_to_home "path/test_post.org"}
                  {:id 2
                   :filename "test_post_series_2.org"
                   :path_relative_to_home "path/test_post_series_2.org"}]))
          (is (every? #(and (:created_at %)
                           (:updated_at %)) parsed-result)))))
    (testing "Get by filename"
      (post-is "test_post.org" (org->json test-post)))
    (testing "Update post"
      (is (= (:status (add-post-request {:filename "test_post.org"
                                         :path_relative_to_home "path/test_post.org"
                                         :post test-series-1}))
             200))
      (post-is "test_post.org" (org->json test-series-2)))))
