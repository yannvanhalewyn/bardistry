(ns bardistry.songlist.db
  (:require
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]
   [bardistry.songlist.tx :as songlist.tx]))

(defn create-song! []
  (let [new-song (song/make)]
    (db/execute-mutations! (songlist.tx/create new-song))
    (nav/navigate! "Lyrics" {:song/id (:song/id new-song)
                             :open-form? true})))

(defn delete-song! [song-id]
  (db/execute-mutations! (songlist.tx/delete song-id))
  (nav/navigate! "Songs"))

(defn all-songs []
  (vals (:songs @db/db)))

(defn find-by-id [id]
  (get-in @db/db [:songs id]))

(defn update! [song-id attrs]
  (db/execute-mutations! (songlist.tx/update* song-id attrs)))

(defn create-section! [song-id]
  (db/execute-mutations! (songlist.tx/create-section song-id)))

(defn update-section! [song-id section-id text]
  (db/execute-mutations!
   (songlist.tx/update-section song-id section-id text)))

(defn delete-section! [song-id section-id]
  (db/execute-mutations! (songlist.tx/delete-section song-id section-id)))

(defn highlight-section! [song-id section-id highlight?]
  (db/execute-mutations!
   (songlist.tx/highlight-section song-id section-id highlight?)))
