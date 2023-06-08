(ns bardistry.songlist.db
  (:require
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]))

(defn create-song! []
  (let [new-song (song/make)]
    (swap! db/db update :songs assoc-in [:songs (:song/id new-song)] new-song)
    (nav/navigate! "Lyrics" {:song/id new-song})))
