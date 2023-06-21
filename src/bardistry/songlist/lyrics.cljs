(ns bardistry.songlist.lyrics
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [bardistry.song :as song]
   [bardistry.db :as db]
   [bardistry.songlist.db :as songlist.db]))

(def Lyrics
  (r/adapt-react-class
   (.-default (js/require "../../../src/bardistry/songlist/Lyrics.js"))))

(defn- ->ui [{:song/keys [id title artist] :as song}]
  (clj->js
   {:id id
    :title title
    :artist artist
    :sections
    (for [{:section/keys [title id lines highlight?]} (song/sections song)]
      {:id id
       :title title
       :highlight highlight?
       :body (str/join "\n" lines)})}))

(defn toggle-form! []
  (db/toggle-ui! ::song-form))

(defn open-form! []
  (db/set-ui! ::song-form true))

(defn close-form! []
  (db/set-ui! ::song-form false))

(defn component []
  (fn [{:keys [:song/id]}]
    (let [song (songlist.db/find-by-id id)]
      [Lyrics {:song (when song (->ui song))
               :onSheetClose close-form!
               :onEditTitle #(songlist.db/update! (:song/id song) {:song/title %})
               :onEditArtist #(songlist.db/update! (:song/id song) {:song/artist %})
               :onSectionAdd #(songlist.db/create-section! (:song/id song))
               :onSectionDelete #(songlist.db/delete-section! (:song/id song) %)
               :onSectionEdit #(songlist.db/update-section! (:song/id song) %1 %2)
               :isSheetOpen (db/get-ui! ::song-form)
               :setHighlight #(songlist.db/highlight-section! (:song/id song) %1 %2)}])))
