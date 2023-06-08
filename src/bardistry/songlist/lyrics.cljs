(ns bardistry.songlist.lyrics
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [bardistry.db :as db]))

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

(defn toggle-edit []
  (swap! db/db update-in [::db/ui ::lyrics-bottom-sheet] not))

(defn component [{:keys [:song/id]}]
  (if-let [song (db/song-by-id id)]
    [Lyrics {:song (process-song song)
             :onSheetClose #(do
                              (.log js/console "On Sheet close")
                             (swap! db/db assoc-in [::db/ui ::lyrics-bottom-sheet] false))
             :isSheetOpen (get-in @db/db [::db/ui ::lyrics-bottom-sheet])}]
    (.error js/console "Could not find song for id:" id)))
