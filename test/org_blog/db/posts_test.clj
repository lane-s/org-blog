(ns org-blog.db.posts-test
  (:require [clojure.test :refer :all]
            [org-blog.db.posts :refer :all]
            [org-blog.db-test :refer [db-fixture test-db]]))

(use-fixtures :once db-fixture)

(deftest test-posts
  (testing "There are no initial posts"
    (is (= (count (get-all test-db)) 0)))
  (testing "Inserting a post is successful"
    (insert test-db {:filename "test" :path_relative_to_home "path/test" :post "test body"})
    (let [posts (get-all test-db)]
      (is (= (select-keys (first posts) [:id :filename :path_relative_to_home])
             {:id 1
              :filename "test"
              :path_relative_to_home "path/test"}))
      (is (= (count posts) 1))))
  (testing "Getting a post by filename"
    (is (= (:id (get-by-filename test-db {:filename "test"}))
           1))))

