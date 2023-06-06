(ns bardistry.db
  (:require
   [bardistry.transit :as transit]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(def HOST "192.168.5.180")
(def PORT 8080)

(defn- api-url [endpoint]
  (str "http://" HOST ":" PORT "/" endpoint))

(defn load-songs! []
  (-> (p/let [res (js/fetch (api-url "songs"))
              body (.text res)]
        (swap! db assoc :songs (transit/read body)))
      (p/catch js/console.error)))
