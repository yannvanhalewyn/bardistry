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

(defn edit-section! [song-id section-id text]
  (let [mutations (songlist.tx/update-section-content song-id section-id text)]
    (db/execute-mutations! mutations)))

(defn append-section! [song-id]
  (db/execute-mutations! (songlist.tx/append-section song-id)))
