(ns bardistry.core
  (:require
   [reagent.core :as r]
   [react-native :as rn]
   [bardistry.db :as db]
   [clojure.string :as str]))

(def SongList (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))
(def App (r/adapt-react-class (.-default (js/require "../../src/bardistry/App.js"))))

(def view (r/adapt-react-class rn/View))
(def text (r/adapt-react-class rn/Text))

(defn song-list []
  (db/load-songs!)
  (fn []
    [SongList
     {:songs (for [song (:songs @db/db)]
               (update song :song/contents #(str/trim (str/join "\n" %))))}]))

(defn app-root []
  [App {:screens
        [{:name "Songs" :component #(r/as-element [song-list])}]}])

(defn ^:export -main
  []
  (r/as-element [app-root]))
