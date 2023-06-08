(ns bardistry.song
  (:require
   [clojure.string :as str]))

(defn make []
  {:song/id (random-uuid)
   :song/title "New Title"
   :song/artist "New Artist"
   :song/contents ["line 1" "line 2"]})

(defn sort-artist [{:keys [:song/artist]}]
  (str/replace artist (re-pattern "(?i)^\\s*the\\s*") ""))
