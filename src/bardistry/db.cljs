(ns bardistry.db
  (:require
   [bardistry.transit :as transit]
   [promesa.core :as p]
   [reagent.core :as r]))

(defonce db (r/atom {}))

(defn load-songs! []
  (.log js/console "LOADING songs.json")
  (try
    (p/let [res (.catch (js/fetch "http://localhost:8080/songs.json")
                        #(.error js/console ".catch" %))
            body (.catch (.text res) #(.error js/console "error" %))]
      (.log js/console "Songs loaded")
      (swap! db assoc :songs (transit/read body)))
    (catch js/Error e
      (.error js/console "CATCH" e)
      )))
