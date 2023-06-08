(ns bardistry.songlist.db
  (:require
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]))

(defn create-song! []
  (let [new-song (song/make)]
    (swap! db/db update :songs assoc-in [:songs (:song/id new-song)] new-song)
    (nav/navigate! "Lyrics" {:song/id new-song})))

(defn update-song [song key value]
  (case key
    "title" (assoc song :song/title value)
    "artist" (assoc song :song/artist value)))

(defn all-songs []
  (vals (:songs @db/db)))

(defn find-by-id [id]
  (get-in @db/db [:songs id]))

(defn update-song! [id key value]
  (when (contains? (:songs @db/db) id)
    (swap! db/db update-in [:songs id]
           update-song key value)))
