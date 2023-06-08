(ns bardistry.db
  (:require
   [bardistry.http :as http]
   [bardistry.storage :as storage]
   [bardistry.util :as u]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(defn load-songs! []
  (swap! db assoc :loading? true)
  (http/load!
   {::http/endpoint "songs"
    ::http/on-failure #(swap! db assoc :loading? false)
    ::http/on-success
    (fn [songs]
      (let [songs (u/key-by :song/id songs)]
        (storage/store-clj "bardistry.songs" songs)
        (swap! db assoc :loading? false :songs songs)))}))

(defn hydrate! []
  (p/let [songs (storage/retreive-clj "bardistry.songs")]
    (swap! db assoc :songs songs)))

(defn loading? []
  (:loading? @db))
