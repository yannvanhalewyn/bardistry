(ns bardistry.songlist.db
  (:require
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]))

(defn create-song! []
  (let [new-song (song/make)]
    (swap! db/db update :songs conj new-song)
    (nav/navigate! "Lyrics" {:song/id (:song/id new-song)})))
