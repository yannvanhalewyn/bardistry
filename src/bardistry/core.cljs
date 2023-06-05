(ns bardistry.core
  (:require
   [reagent.core :as r]
   [bardistry.db :as db]
   [clojure.string :as str]
   [applied-science.js-interop :as j]))

(def SongList (r/adapt-react-class (.-default (js/require "../../src/bardistry/SongList.js"))))
(def Lyrics (r/adapt-react-class (.-default (js/require "../../src/bardistry/Lyrics.js"))))
(def App (r/adapt-react-class (.-default (js/require "../../src/bardistry/App.js"))))

;; (defn collapse [collapse-el]
;;   (fn [rf]
;;     (fn
;;       ([rf] rf)
;;       ([rf x]
;;        (rf x))
;;       ([rf acc x]
;;        ))))

(defn collapse-xf [collapse-el]
  (fn [rf]
    (let [seen (atom nil)]
      (fn
        ([] (rf))
        ([result] (rf result))
        ([result input]
         (let [repeat? (and @seen (= input collapse-el))]
           (if (= input collapse-el)
             (reset! seen true)
             (reset! seen false))
           (if repeat?
             result
             (rf result input))))))))

(defn collapse [collapse-el coll]
  (reduce (fn [out el]
            (if (and (= el collapse-el)
                     (= (last out) collapse-el))
              out
              (conj out el)))
          [] coll))

(defn- process-section [[header-or-line & lines :as all-lines]]
  (if-let [[_ section-title] (re-find #"\[(.*)\]" header-or-line)]
    {:section/title section-title
     :section/lines lines}
    {:section/lines all-lines}))

(defn- prepare-song-contents [contents]
  (->> contents
       (collapse "")
       (partition-by #(= % ""))
       (remove #(= % [""]))
       (map process-section)
       (map (fn [song] (update song :section/lines #(str/trim (str/join "\n" %)))))))

(comment
  (->> (:song/contents (first (:songs @db/db)))
       (collapse "")
       (partition-by #(= % ""))
       (remove #(= % [""]))
       (map process-section)
       (map (fn [song] (update song :section/lines #(str/trim (str/join "\n" %)))))
       )

  )

(defn app-root []
  (db/load-songs!)
  (fn []
    (let [songs (for [song (:songs @db/db) #_(sort-by :song/sort-artist (:songs @db/db))]
                  (update song :song/contents prepare-song-contents))
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
