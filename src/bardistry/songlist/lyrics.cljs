(ns bardistry.songlist.lyrics
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [bardistry.song :as song]
   [bardistry.db :as db]
   [bardistry.songlist.db :as songlist.db]))

(def Lyrics
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/Lyrics.js"))))

(defn- ->ui [{:song/keys [id title artist] :as song}]
  (clj->js
   {:id id
    :title title
    :artist artist
    :sections (for [{:section/keys [title id lines]} (song/sections song)]
                {:id id
                 :title title
                 :isChorus (when title
                             (and (str/includes? (str/lower-case title) "chorus")
                                  (not (str/includes? (str/lower-case title) "pre"))))
                 :body (str/join "\n" lines)})}))

(defn toggle-form! []
  (swap! db/db update-in [::db/ui ::song-form] not))

(defn open-form! []
  (swap! db/db assoc-in [::db/ui ::song-form] true))

(defn close-form! []
  (swap! db/db assoc-in [::db/ui ::song-form] false))

(defn component [{:keys [open-form?]}]
  (if open-form?
    (open-form!)
    ;; Would be better kept as local state maybe, so that it gets reset
    ;; automatically when opening a new song.
    (close-form!))
  (fn [{:keys [:song/id]}]
    (if-let [song (songlist.db/find-by-id id)]
      [Lyrics {:song (->ui song)
               :onSheetClose close-form!
               :onSectionEdit #(songlist.db/edit-section! (:song/id song) %1 %2)
               :onSongEdit songlist.db/update-song!
               :isSheetOpen (get-in @db/db [::db/ui ::song-form])}]
      (.error js/console "Could not find song for id:" id))))
