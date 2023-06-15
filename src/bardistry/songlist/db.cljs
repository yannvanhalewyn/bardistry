(ns bardistry.songlist.db
  (:require
   [bardistry.api :as api]
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]
   [bardistry.songlist.tx :as songlist.tx]
   [clojure.string :as str]))

(defn create-song! []
  (let [new-song (song/make)]
    (swap! db/db assoc-in [:songs (:song/id new-song)] new-song)
    (nav/navigate! "Lyrics" {:song/id (:song/id new-song) :open-form? true})))

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
