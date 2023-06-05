(ns bardistry.core
  (:require
   [reagent.core :as r]
   [react-native :as rn]
   [bardistry.db :as db]
   [clojure.string :as str]
   [applied-science.js-interop :as j]))

(def SongList (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))
(def Lyrics (r/adapt-react-class (.-default (js/require "../../src/bardistry/Lyrics.js"))))
(def App (r/adapt-react-class (.-default (js/require "../../src/bardistry/App.js"))))

(def view (r/adapt-react-class rn/View))
(def text (r/adapt-react-class rn/Text))

(def args (atom nil))

(defn app-root []
  (db/load-songs!)
  (fn []
    (let [songs (for [song (sort-by :song/sort-artist (:songs @db/db))]
                  (update song :song/contents #(str/trim (str/join "\n" %))))
          find-song (fn [id] (first (filter #(= (:song/id %) id) songs)))]
      [App {:screens
            [{:name "Songs"
              :component
              #(r/as-element
               [SongList {:songs songs}])}
             {:name "Lyrics"
              :component #(let [id (j/get-in % [:route :params :id])]
                            (r/as-element [Lyrics {:song (find-song id)}]))}]}])))

(defn ^:export -main
  []
  (r/as-element [app-root]))
