(ns bardistry.db
  (:require
   [bardistry.transit :as transit]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(defn load-songs! []
  (.log js/console "LOADING songs.json")
  (p/let [res (js/fetch "http://localhost:8080/songs.json")
          body (.text res)]
    (.log js/console "Songs loaded")
    (swap! db assoc :songs (transit/read body))))
