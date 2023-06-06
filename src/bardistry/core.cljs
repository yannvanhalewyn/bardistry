(ns bardistry.core
  (:require
   [reagent.core :as r]
   [bardistry.db :as db]
   [clojure.string :as str]
   [applied-science.js-interop :as j]))

(def SongList
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/SongList.js"))))

(def Lyrics
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/Lyrics.js"))))

(def App
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/App.js"))))

(defn collapse [collapse-el coll]
  (reduce (fn [out el]
            (if (and (= el collapse-el)
                     (= (last out) collapse-el))
              out
              (conj out el)))
          [] coll))

(defn- parse-section-title [line]
  (or (second (re-find #"\[(.*)\]" line))
      (when (contains? #{"chorus" "verse"} (str/lower-case line))
        line)))

(defn- process-section [[header-or-line & lines :as all-lines]]
  (let [section-title (parse-section-title header-or-line)]
    (-> (if section-title
          {:section/title section-title
           :section/chorus? (= (str/lower-case section-title) "chorus")
           :section/lines lines}
          {:section/lines all-lines})
        (assoc :section/id (random-uuid)))))

(defn- prepare-song-contents [contents]
  (->> contents
       (collapse "")
       (partition-by #(= % ""))
       (remove #(= % [""]))
       (map process-section)
       (map (fn [song] (update song :section/lines #(str/trim (str/join "\n" %)))))))

(defn- process-song [song]
  (when song
    (assoc song
      :song/processed-lyrics (prepare-song-contents (:song/contents song)))))

(defn app-root []
  (db/load-songs!)
  (fn []
    (let [songs (sort-by :song/sort-artist (:songs @db/db))
          find-song (fn [id] (first (filter #(= (:song/id %) id) songs)))]
      [App {:screens
            [{:name "Songs"
              :component
              #(r/as-element
               [SongList {:songs songs}])}
             {:name "Lyrics"
              :component #(let [id (j/get-in % [:route :params :id])]
                            (r/as-element [Lyrics {:song (process-song (find-song id))}]))}]}])))

(defn ^:export -main
  []
  (r/as-element [app-root]))
