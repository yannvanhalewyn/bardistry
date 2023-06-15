(ns bardistry.songlist.tx)

(def tx-fns
  {:song/assoc-in
   '(fn [ctx song-id path value]
     (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
       (when (contains? (get-in song [:song/lyrics :lyrics/sections]) section-id)
         ;; TODO compare old and new and reject if not the same
         ;; TODO validate against schema? Or have a biff-tx with an assoc-in validator
         [[:xtdb.api/put (assoc-in song path value)]])))})

(defn set-lines [song-id section-id lines]
  [[:xtdb.api/fn
    :song/assoc-in song-id
    [:song/lyrics :lyrics/sections section-id :section/lines]
    (vec lines)]])
