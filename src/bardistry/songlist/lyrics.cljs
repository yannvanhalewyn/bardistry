(ns bardistry.songlist.lyrics
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [bardistry.db :as db]
   [bardistry.songlist.db :as songlist.db]))

(def Lyrics
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/Lyrics.js"))))

(defn collapse [collapse-el coll]
  (reduce (fn [out el]
            (if (and (= el collapse-el)
                     (= (last out) collapse-el))
              out
              (conj out el)))
          [] coll))

(defn- parse-section-title [line]
  (second
   (re-find (re-pattern "(?i)\\[?((?:chorus|verse|bridge|intro|outro)\\s*\\d*)\\]?$") line)))

(defn- process-section [[header-or-line & lines :as all-lines]]
  (let [section-title (parse-section-title header-or-line)]
    (-> (if section-title
          {:section/title section-title
           :section/chorus? (str/includes? (str/lower-case section-title) "chorus")
           :section/lines lines}
          {:section/lines all-lines})
        (assoc :section/id (random-uuid)))))

(defn- prepare-song-contents [contents]
  (->> contents
       (collapse "")
       (partition-by #(= % ""))
       (remove #(= % [""]))
       (map process-section)
       (map (fn [{:keys [:section/lines] :as section}]
              (assoc section :section/body (str/trim (str/join "\n" lines)))))))

(defn- process-song [song]
  (assoc song
    :song/processed-lyrics (prepare-song-contents (:song/contents song))))

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
      [Lyrics {:song (process-song song)
               :onSheetClose close-form!
               :onSongEdit songlist.db/update-song!
               :isSheetOpen (get-in @db/db [::db/ui ::song-form])}]
      (.error js/console "Could not find song for id:" id))))
