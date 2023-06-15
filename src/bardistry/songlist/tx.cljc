(ns bardistry.songlist.tx
  (:require
   [clojure.string :as str]))

(def tx-fns
  {:song/assoc-in
   '(fn [ctx song-id path value]
     (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
       (when (contains? (get-in song [:song/lyrics :lyrics/sections]) section-id)
         ;; TODO compare old and new and reject if not the same
         ;; TODO validate against schema? Or have a biff-tx with an assoc-in validator
         [[:xtdb.api/put (assoc-in song path value)]])))})

(defn update-section-content [song-id section-id lines]
  (let [[title & lines] (str/split-lines lines)]
    [[:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/lines]
      (vec lines)]
     [:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/title] title]]))

(defn apply-mutations [songs-by-id mutations]
  (reduce
   (fn [songs-by-id [mutation & params]]
     (case mutation
       :song/assoc-in
       (let [[song-id path value] params]
         (assoc-in songs-by-id (concat [song-id] path) value))))
   songs-by-id
   mutations))

(defn mutations->tx [mutations]
  (for [[mutation & params] mutations]
    (case mutation
      :song/assoc-in
      (let [[song-id path value] params]
        [:xtdb.api/fn :song/assoc-in song-id path value]))))
