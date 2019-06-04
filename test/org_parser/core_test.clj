(ns org-parser.core-test
  (:require [clojure.test :refer :all]
            [org-parser.core :refer :all]))

(def test-path "./test/org_parser/")
(def test-post (slurp (str test-path "test_post.org")))
(def test-series-1 (slurp (str test-path "test_post_series_1.org")))
(def test-series-2 (slurp (str test-path "test_post_series_2.org")))

(def parsed-post
  `({:heading "Post Title",
     :body
     ({:type :plaintext, :body ("This is the introductory paragraph." "")}
      {:type              :code-block,
       :src-type          "clojure",
       :body              ("(+ 1 2)"),
       :execution-results ": 3"}
      {:type :plaintext,
       :body
       (""
        "Here's another little paragraph with some more text."
        "")}),
     :subheadings
     ({:heading     "Heading one",
       :body        ({:type :plaintext, :body ("Here's the first section." "")}),
       :subheadings ()}
      {:heading "Heading two",
       :body
       ({:type :plaintext,
         :body
         ("Here's the first paragraph of the first section."
          ""
          "Here's the next paragraph."
          "")}),
       :subheadings
       ({:heading "This is a subheading", :body (), :subheadings ()})}
      {:heading     "Heading three",
       :body        ({:type :plaintext, :body ("Here's the last paragraph" "")}),
       :subheadings ()})}
    {:heading     "Next top level",
     :body        ({:type :plaintext, :body ("Here's the content" "")}),
     :subheadings ()}))

(def parsed-metadata {:category "Clojure"})

(deftest metadata-line->kv-pair-test
  (testing "Splits input into a keyword and value"
    (is (= (metadata-line->kv-pair ":CATEGORY: Clojure")
           [:category "Clojure"]))))

(deftest strip-metadata-test
  (let [[metadata after-metadata] (-> test-post file-contents->trimmed-lines strip-metadata)]
    (testing "Returns correct metadata object"
      (is (= metadata parsed-metadata)))))

(deftest parse-post-test
  (testing "Returns parsed post"
    (is (= (-> test-post
               file-contents->trimmed-lines
               strip-metadata
               second
               parse-post)
           parsed-post))))

(deftest parse-test
  (testing "Returns parsed post"
    (is (= (parse test-post)
           {:post parsed-post
            :metadata parsed-metadata}))))
