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

(defn edit-section! [song-id section-id text]
  (let [mutations (songlist.tx/update-section-content song-id section-id text)]
    (swap! db/db update :songs songlist.tx/apply-mutations mutations)

    (api/request!
     {::api/endpoint "mutate"
      ::api/method :post
      ::api/params
      {:db/tx-ops (songlist.tx/mutations->tx mutations)}
      ::api/on-success #(.log js/console "mutate success" (clj->js %))})))

(defn append-section! [song-id]
  (let [mutations (songlist.tx/append-section song-id)]
    (swap! db/db update :songs songlist.tx/apply-mutations mutations)

    (api/request!
     {::api/endpoint "mutate"
      ::api/method :post
      ::api/params
      {:db/tx-ops (songlist.tx/mutations->tx mutations)}
      ::api/on-success #(.log js/console "mutate success" (clj->js %))})))
