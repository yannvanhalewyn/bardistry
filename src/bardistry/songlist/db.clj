(ns bardistry.songlist.db
  (:require
   [bardistry.song :as song]
   [com.biffweb :as biff]
   [xtdb.api :as xt]))

(defn create-song-tx [song]
  [::xt/put (assoc song :xt/id (:song/id song))])

(defn delete-song-tx [song-id]
  [::xt/delete song-id])

(defn persist-song! [ctx song]
  (biff/submit-tx ctx [(create-song-tx song)]))

(defn delete-song! [ctx song-id]
  (biff/submit-tx ctx [(delete-song-tx song-id)]))

(defn configure-tx-fn! [ctx]
  (biff/submit-tx
   ctx
   [[::xt/put
     {:xt/id :section/set-lines
      :xt/fn
      '(fn [ctx song-id section-id new-lines]
         (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
           (when (contains? (get-in song [:song/lyrics :lyrics/sections]) section-id)
             [[::xt/put
               (assoc-in
                song
                [:song/lyrics :lyrics/sections section-id :section/lines]
                new-lines)]])))}]]))

(defn set-section-lines! [ctx song-id section-id new-lines]
  (biff/submit-tx ctx [[::xt/fn :section/set-lines song-id section-id new-lines]]))
