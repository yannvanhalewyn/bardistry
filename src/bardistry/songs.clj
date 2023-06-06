(ns bardistry.songs
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn- process-song [[title artist & contents]]
  {:song/id (random-uuid)
   :song/title title
   :song/artist artist
   :song/sort-artist (str/replace artist (re-pattern "(?i)^\\s*the\\s*") "")
   :song/contents (into [] contents)})

(defn read-songs! [filename]
  (with-open [r (io/reader (io/file filename))]
    (into []
          (comp (partition-by #(= % "{{SONG}}"))
                (remove #(= % ["{{SONG}}"]))
                (map process-song))
          (line-seq r))))
