(ns bardistry.songlist.lyrics
  (:require
   [clojure.string :as str]
   [reagent.core :as r]))

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
   (or (re-find #"\[(.*)\]" line)
       (re-find (re-pattern "(?i)^(chorus|verse\\s*\\d*|bridge)$") line))))

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

(defn component [{:keys [song]}]
  [Lyrics {:song (process-song song)}])
