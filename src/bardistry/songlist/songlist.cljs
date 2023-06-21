(ns bardistry.songlist.songlist
  (:require
   [applied-science.js-interop :as j]
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.song :as song]
   [bardistry.songlist.db :as songlist.db]
   [clojure.string :as str]
   [reagent.core :as r]))

(def SongList
  (r/adapt-react-class
   (.-default (js/require "../../../src/bardistry/songlist/SongList.js"))))

(defn- song-matcher [q]
  (let [q (str/lower-case q)]
    (fn [song]
      (str/includes?
       (str (str/lower-case (:song/title song))
            (str/lower-case (:song/artist song))
            (str/lower-case (str/join " " (:song/tags song))))
       q))))

(defn component []
  (let [query (r/atom "")]
    (fn []
      (let [songs (songlist.db/all-songs)]
        [SongList
         {:songs (->> (if-let [q @query]
                        (filter (song-matcher q) songs)
                        songs)
                      (sort-by song/sort-artist))
          :isLoading (db/loading?)
          :loadSongs db/load-songs!

          :onQueryChange #(reset! query %)
          :onClearSearch #(reset! query "")
          :showClearSearch (not-empty @query)

          :isFormOpen (db/get-ui! ::song-form)
          :onOpenForm #(db/toggle-ui! ::song-form)
          :onCloseForm #(db/set-ui! ::song-form false)
          :onCreateSong (fn [form]
                          (db/set-ui! ::song-form false)
                          (songlist.db/create-song!
                           {:song/title (j/get form :title)
                            :song/artist (j/get form :artist)}))

          :onDeleteSong songlist.db/delete-song!
          :onSongPress #(nav/navigate! "Lyrics" {:song/id %})}]))))
