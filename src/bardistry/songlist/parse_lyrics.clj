(ns bardistry.songlist.parse-lyrics
  (:require
   [clojure.string :as str]))

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

(defn- line->entry [line]
  (if-let [title (parse-section-title line)]
    [:section/title title]
    [:section/line line]))

(defn- split-on-title-entries [[sections cur-section] entry]
  (if (= :section/title (first entry))
    [(cond-> sections cur-section (conj cur-section)) [entry]]
    [sections (conj cur-section entry)]))

(defn end-split [[sections cur-section]]
  (conj sections cur-section))

(defn- make-section [[header-or-line & other-entries :as all-entries]]
  (let [title (when (= (first header-or-line) :section/title)
                (second header-or-line))
        lines (if title other-entries all-entries)]
    {:section/title title
     :section/chorus? (str/includes? (str/lower-case title) "chorus")
     :section/lines (mapv second lines)}))

(defn lines->lyrics [lines]
  (->>
   (collapse "" lines)
   (map line->entry)
   (reduce split-on-title-entries [[] nil])
   (end-split)
   (map make-section)
   ))
