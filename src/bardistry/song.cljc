(ns bardistry.song
  (:require
   [clojure.string :as str]))

(defn make []
  {:song/id (random-uuid)
   :song/title ""
   :song/artist ""
   :song/contents [" "]})

(defn sort-artist [{:keys [:song/artist]}]
  (str/replace artist (re-pattern "(?i)^\\s*the\\s*") ""))
