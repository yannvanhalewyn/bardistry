(ns bardistry.util)

(defn key-by
  "Returns a map of the elements of coll keyed by the value of
  applying f to each element in coll.

  @example
  (def users [{:name \"John\" :id 2} {:name \"Jeff\" :id 3}])
  (key-by :id users) ;; => {2 {:name \"John\" :id 2} 3 {:name \"Jeff\" :id 3}}"
  [f coll]
  (into {} (for [r coll] [(f r) r])))
