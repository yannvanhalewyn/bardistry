(ns bardistry.songlist.tx
  (:require
   [clojure.string :as str]))

(def tx-fns
  ;; TODO compare old and new and reject if not the same
  ;; TODO validate against schema? Or have a biff-tx with an assoc-in validator
  {:song/assoc-in
   '(fn [ctx song-id path value]
     (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
       [[:xtdb.api/put (assoc-in song path value)]]))

   :song/append-section
   '(fn [ctx song-id section-id]
     (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
       [[:xtdb.api/put (update-in song [:song/lyrics :lyrics/arrangement] conj section-id)]]))})

(defn update-section-content [song-id section-id lines]
  (let [[title & lines] (str/split-lines lines)]
    [[:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/lines]
      (vec lines)]
     [:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/title] title]]))

(defn append-section [song-id]
  (let [section-id (random-uuid)]
    [[:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/lines]
      ["Section Title" ""]]
     [:song/append-section song-id section-id]]))

(defn apply-mutations [songs-by-id mutations]
  (reduce
   (fn [songs-by-id [mutation & params]]
     (case mutation
       :song/assoc-in
       (let [[song-id path value] params]
         (assoc-in songs-by-id (concat [song-id] path) value))
       :song/append-section
       (let [[song-id section-id] params]
         (update-in songs-by-id [song-id :song/lyrics :lyrics/arrangement]
                    conj section-id))))
   songs-by-id
   mutations))

(defn mutations->tx [mutations]
  (for [[mutation & params] mutations]
    (case mutation
      :song/assoc-in
      (let [[song-id path value] params]
        [:xtdb.api/fn :song/assoc-in song-id path value])
      :song/append-section
      (let [[song-id section-id] params]
        [:xtdb.api/fn :song/append-section song-id section-id]))))
