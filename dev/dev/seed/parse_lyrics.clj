(ns dev.seed.parse-lyrics
  (:require
   [medley.core :as m]))

(defn collapse-xf [collapse-item]
  (let [last-item (atom nil)]
    (fn [rf]
      (fn
        ([] (rf))
        ([result] (rf result))
        ([result input]
         (let [prev @last-item]
           (reset! last-item input)
           (if (= input collapse-item prev)
             result
             (rf result input))))))))

(defn- parse-section-title [line]
  (second
   (re-find (re-pattern "(?i)\\[?((?:chorus|verse|bridge|intro|outro)\\s*\\d*)\\]?$") line)))

(defn- line->entry [line]
  (if-let [title (parse-section-title line)]
    [:section/title title]
    [:section/line line]))

(defn title? [entry]
  (= (first entry) :section/title))

(defn- split-sections-xf [rf]
  (let [cur-section (atom [])]
    (fn
      ([] (rf))
      ([result]
       (if-let [cur (not-empty @cur-section)]
         (rf (unreduced (rf result cur)))
         (rf result)))
      ([result input]
       (let [prev-section @cur-section]
         (if (title? input)
           (do (reset! cur-section [input])
               (if (not-empty prev-section)
                 (rf result prev-section)
                 result))
           (do (swap! cur-section conj input)
               result)))))))

(defn- trim-list [coll]
  (->> coll
       (drop-while empty?)
       (reverse)
       (drop-while empty?)
       (reverse)
       (into [])))

(defn- entries->section [[header-or-line & other-entries :as all-entries]]
  (let [id (random-uuid)
        title (when (title? header-or-line)
                (second header-or-line))
        lines (if title other-entries all-entries)]
    {:section/id id
     :section/title title
     :section/lines (trim-list (mapv second lines))}))

(defn lines->lyrics [lines]
  (into []
        (comp (collapse-xf "")
              (map line->entry)
              split-sections-xf
              (remove #(= % [[:section/line ""]]))
              (map entries->section))
        lines))

(defn parse [lines]
  (let [sections (lines->lyrics lines)]
    {:lyrics/sections (m/index-by :section/id sections)
     :lyrics/arrangement (mapv :section/id sections)}))

(comment
  (parse ["Chorus" "Line"])

  (parse
   [""
    "Verse 1"
    "Hey Jude, don't make it bad"
    ""
    ""
    "Sing a sad song"
    "And make it better"
    "Chorus"
    ""
    "And anytime you feel the pain"
    "Hey Jude, refrain"
    ""
    ""
    "Hey Jude, refrain"
    ])



  )
