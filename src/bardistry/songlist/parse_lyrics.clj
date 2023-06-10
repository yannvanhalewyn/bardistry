(ns bardistry.songlist.parse-lyrics)

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
  (let [cur-section (atom nil)]
    (fn
      ([] (rf))
      ([result]
       (if-let [cur @cur-section]
         (rf (unreduced (rf result cur)))
         (rf result)))
      ([result input]
       (let [prev-section @cur-section]
         (if (title? input)
           (do (reset! cur-section [input])
               (if prev-section
                 (rf result prev-section)
                 result))
           (do (swap! cur-section conj input)
               result)))))))

(defn- entries->section [[header-or-line & other-entries :as all-entries]]
  (let [title (when (title? header-or-line)
                (second header-or-line))
        lines (if title other-entries all-entries)]
    {:section/title title
     :section/lines (mapv second lines)}))

(defn lines->lyrics [lines]
  (into []
        (comp (collapse-xf "")
              (map line->entry)
              split-sections-xf
              (map entries->section))
        lines))

(comment
  (lines->lyrics
   ["Verse 1"
    "Hey Jude, don't make it bad"
    ""
    ""
    "Sing a sad song"
    "And make it better"
    "Chorus"
    "And anytime you feel the pain"
    "Hey Jude, refrain"])

  )
