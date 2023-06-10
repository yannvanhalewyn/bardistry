(ns bardistry.db
  (:require
   [bardistry.api :as api]
   [bardistry.storage :as storage]
   [bardistry.util :as u]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(defn load-songs! []
  (swap! db assoc :loading? true)
  (api/request!
   {::api/endpoint "q"
    ::api/method :post
    ::api/params {:query '{:find (pull ?song [:song/id :song/title :song/artist
                                              :song/lyrics])
                           :where [[?song :song/id _]]}}
    ::api/on-failure #(swap! db assoc :loading? false)
    ::api/on-success
    (fn [songs]
      (let [songs (u/key-by :song/id songs)]
        (storage/store-clj "bardistry.songs" songs)
        (swap! db assoc :loading? false :songs songs)))}))

(defn hydrate! []
  (p/let [songs (storage/retreive-clj "bardistry.songs")]
    (swap! db assoc :songs songs)))

(defn loading? []
  (:loading? @db))
