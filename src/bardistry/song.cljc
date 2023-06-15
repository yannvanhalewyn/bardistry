(ns bardistry.song
  (:require
   [bardistry.lyrics :as lyrics]
   [clojure.string :as str]))

(defn make [attrs]
  {:song/id (random-uuid)
   :song/title (:song/title attrs "")
   :song/artist (:song/artist attrs "")
   :song/lyrics (lyrics/make)})

(defn sections [song]
  (lyrics/sections (:song/lyrics song)))

(defn sort-artist [{:keys [:song/artist]}]
  (str/replace artist (re-pattern "(?i)^\\s*the\\s*") ""))
