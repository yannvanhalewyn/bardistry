(ns bardistry.songlist.songlist
  (:require
   [bardistry.db :as db]
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
    (fn []
      (let [songs (for [song (db/get-songs)]
                    ;; TODO don't need to be thinking about types and routing
                    ;; implementation
                    (update song :song/id str))]
        [SongList {:songs (->> (if-let [q @query]
                                 (filter (song-matcher q) songs)
                                 songs)
                               (sort-by :song/sort-artist))
                   :isLoading (db/loading?)
                   :loadSongs db/load-songs!
                   :showClearSearch (not-empty @query)
                   :onClearSearch #(reset! query "")
                   :onQueryChange #(reset! query %)}]))))
