(ns org-blog.helper.coll)

(defn replace-last
  "Replace the last element in `coll`
  with `elem`"
  [coll elem]
  (conj (pop coll) elem))
