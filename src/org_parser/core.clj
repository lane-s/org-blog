(ns org-parser.core
  (:require [clojure.string :as str])
  (:gen-class))

(defmacro map-nested
  [fn pred & body]
  `(map (fn [elem#] (~fn ~pred elem#)) ~@body))

(defmacro map-in
  [pred & body]
  `(map-nested map ~pred ~@body))

(defmacro filter-in
  [pred & body]
  `(map-nested filter ~pred ~@body))

(defn drop-blank
  [coll]
  (drop-while str/blank? coll))

(declare strip-metadata file-contents->trimmed-lines parse-post)
(defn parse
  "Takes an org file as a string and parses it
  into a data structure"
  [org-file-contents]
  (let [[metadata remaining-lines] (-> org-file-contents
                                       file-contents->trimmed-lines
                                       strip-metadata)
        post                       (parse-post remaining-lines)]
    {:post     post
     :metadata metadata}))

(defn file-contents->trimmed-lines
  "Take a file's contents and
  return a sequence of lines
  with whitespace trimmed"
  [file-contents]
  (->> file-contents
       str/split-lines
       (map str/trim)))

(declare split-with-exclusive metadata-line->kv-pair)
(defn strip-metadata
  "Takes a list of org file lines
  Returns the metadata as a map
  and the remaining lines"
  [org-file-lines]
  (let [[metadata-lines remaining-lines] (split-with-exclusive #(not= % ":END:") org-file-lines)
        metadata (->> metadata-lines
                      drop-blank
                      (drop 1) ;; Drop the :BLOG_METADATA: line
                      (map metadata-line->kv-pair)
                      (into {}))]
    [metadata remaining-lines]))

(defn split-with-exclusive
  "split-with that doesn't include the
  first element that satisfies the predicate in the result"
  [pred coll]
  (let [[before after] (split-with pred coll)]
    [before (drop 1 after)]))

(defn metadata-line->kv-pair
  "Splits metadata line into a key value pair
  Format :KEY: Value Text Here"
  [line]
  (let [[before-last-colon remaining] (->> line
                                           (drop 1) ;; Drop the first colon
                                           (split-with-exclusive #(not= % \:))
                                           (map str/join))
        k (->> before-last-colon
               str/lower-case
               keyword)
        v (->> remaining
               str/trim)]
    [k v]))

(declare is-heading? parse-body)
(defn parse-post
  "Takes lines of a post
  and parses into a tree structure"
  ([lines] (parse-post lines 1))
  ([lines depth]
   (->> lines
        (drop-while #(-> % first (not= \*))) ;; Advance to first heading
        (partition-by #(is-heading? % depth))
        (partition 2)                   ;; Group each heading with all lines under the heading
        (map (fn [[heading section]]
               (let [[body subheadings]
                     (split-with #(not (str/starts-with? % "*")) section)] ;; Get lines before and after the first subheading
                 {:heading (->> heading
                                first
                                (drop depth)
                                str/join
                                str/trim)
                  :body (parse-body body)
                  :subheadings (parse-post subheadings (inc depth))}))))))

(defn is-heading?
  [line heading-level]
  (and (= (take heading-level line)
          (take heading-level (repeat \*)))
       (= (nth line heading-level)
          \space)))

(def src-begin "#+BEGIN_SRC")
(def src-end "#+END_SRC")
(def src-results "#+RESULTS")

(declare add-code-block in-code-block-body?
         is-result-row? update-code-block-with-results)
(defn parse-body
  "Parse the body under a heading
  into paragraphs, images, and code
  blocks"
  [body]
  (->> body
       ;; FIXME Would it be possible to write a partition function that works better for this case and makes the reducer less complex?
       (partition-by (fn [line]
                       (let [line-starts-with (partial str/starts-with? line)]
                         (or (line-starts-with src-begin)
                             (line-starts-with src-end)
                             (line-starts-with src-results)))))
       (map drop-blank)
       (filter (complement empty?))
       (reduce (fn [parsed next-partition]
                 (let [[first-line & remaining] next-partition
                       line-starts-with         (partial str/starts-with? first-line)
                       last-parsed              (first parsed)
                       assoc-last               #(cons (assoc last-parsed %1 %2) (rest parsed))]
                   (cond
                     (line-starts-with src-begin)      (add-code-block first-line parsed)
                     (line-starts-with src-end)        parsed
                     (in-code-block-body? last-parsed) (assoc-last :body next-partition)

                     (line-starts-with src-results) (assoc-last :execution-results nil)
                     (is-result-row? last-parsed)   (update-code-block-with-results assoc-last next-partition)

                     :else (cons {:type :paragraph :body next-partition} parsed))))
               '())
       reverse))

(defn add-code-block
  [line coll]
  (cons {:type     :code-block
         :src-type (second (str/split line #" "))} coll))

(defn in-code-block-body?
  [last-parsed]
  (and (= (:type last-parsed) :code-block)
       (not (:body last-parsed))))

(defn is-result-row?
  [last-parsed]
  (and (contains? last-parsed :execution-results)
       (nil? (:execution-results last-parsed))))

(defn update-code-block-with-results
  [assoc-last [line & remaining]]
  (cond->>
   (assoc-last :execution-results line)
    (not-empty remaining) (cons {:type :paragraph :body remaining})))

;; (-> "./test/org_parser/test_post.org" slurp file-contents->trimmed-lines strip-metadata second parse-post)
