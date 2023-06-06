(ns bardistry.core
  (:require
   [applied-science.js-interop :as j]
   [bardistry.db :as db]
   [bardistry.songlist.songlist :as songlist]
   [bardistry.songlist.lyrics :as lyrics]
   [reagent.core :as r]))

(def App
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/App.js"))))

(defn app-root []
  (db/load-songs!)
  (fn []
    (let [songs (sort-by :song/sort-artist (:songs @db/db))
          find-song (fn [id] (first (filter #(= (:song/id %) id) songs)))]
      [App {:screens
            [{:name "Songs"
              :component
              #(r/as-element [songlist/component {:songs songs}])}
             {:name "Lyrics"
              :component
              #(let [id (j/get-in % [:route :params :id])]
                 (r/as-element [lyrics/component {:song (find-song id)}]))}]}])))

(defn ^:export -main
  []
  (r/as-element [app-root]))
