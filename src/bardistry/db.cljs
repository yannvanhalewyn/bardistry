(ns bardistry.db
  (:require
   [bardistry.transit :as transit]
   [bardistry.storage :as storage]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(def HOST "192.168.5.180")
(def PORT 8080)

(defn- api-url [endpoint]
  (str "http://" HOST ":" PORT "/" endpoint))

(defn load-songs! []
  (-> (p/let [res (js/fetch (api-url "songs"))
              body (.text res)
              songs (transit/read body)]
        (storage/store-clj "bardistry.songs" songs)
        (swap! db assoc :songs songs))
      (p/catch js/console.error)))

(defn hydrate! []
  (p/let [songs (storage/retreive-clj "bardistry.songs")]
    (swap! db assoc :songs songs)))
