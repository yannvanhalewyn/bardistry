(ns bardistry.song
  (:require
   [bardistry.lyrics :as lyrics]
   [clojure.string :as str]))

(defn make []
  {:song/id (random-uuid)
   :song/title ""
   :song/artist ""
   :song/lyrics (lyrics/make)})

(defn sort-artist [{:keys [:song/artist]}]
  (str/replace artist (re-pattern "(?i)^\\s*the\\s*") ""))
