(ns bardistry.songlist.load-songs
  (:require
   [bardistry.songlist.parse-lyrics :as parse-lyrics]
   [clojure.java.io :as io]
   [xtdb.api :as xt]))

(defonce song-ids (atom {}))

(defn- get-id [title artist]
  (or (get @song-ids [title artist])
      (let [id (random-uuid)]
        (swap! song-ids assoc [title artist] id)
        id)))

(defn- process-song [[title artist & contents]]
  {:song/id (get-id title artist)
   :song/title title
   :song/artist artist
   :song/lyrics (parse-lyrics/parse contents)})

(defn read-songs! [file]
  (with-open [r (io/reader file)]
    (into []
          (comp (partition-by #(= % "{{SONG}}"))
                (remove #(= % ["{{SONG}}"]))
                (map process-song))
          (line-seq r))))

(defn load-songs! [node]
  (xt/submit-tx
   node
   (for [song (read-songs! (io/resource "lyrics.txt"))]
     [::xt/put
      (assoc song :xt/id (:song/id song))])))

(comment
  (def node (:biff.xtdb/node @bardistry.core/system))


  (filter
   #(= (:song/title %) "When You Break")
   (read-songs! (io/resource "lyrics.txt")))

  (load-songs! node)

  (defn clear-db! [node]
    (xt/submit-tx
     node
     (for [id (com.biffweb/q (xt/db node) '{:find ?e :where [[?e :xt/id ?a]]})]
       [::xt/delete id])))

  (clear-db! node)

  )
