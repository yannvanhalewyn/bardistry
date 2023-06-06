(ns bardistry.songlist.songlist
  (:require
   [clojure.string :as str]
   [reagent.core :as r]))

(def SongList
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/SongList.js"))))

(defn- song-matcher [q]
  (let [q (str/lower-case q)]
    (fn [song]
      (str/includes?
       (str (str/lower-case (:song/title song))
            (str/lower-case (:song/artist song)))
       q))))

(defn component []
  (let [query (r/atom "")]
    (fn [{:keys [songs]}]
      [SongList {:songs (if-let [q @query]
                          (do
                            (.log js/console @query q)
                            (filter (song-matcher q) songs))
                          songs)
                 :onQueryChange #(reset! query %)}])))
