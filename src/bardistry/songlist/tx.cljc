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

(defn create [song]
  [[:song/create song]])

(defn update* [song-id params]
  [[:song/update song-id params]])

(defn delete [song-id]
  [[:song/delete song-id]])

(defn update-section-content [song-id section-id lines]
  (let [[title & lines] (str/split-lines lines)]
    [[:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/lines]
      (vec lines)]
     [:song/assoc-in song-id
      [:song/lyrics :lyrics/sections section-id :section/title] title]]))

(defn append-section [song-id]
  (let [section-id (random-uuid)]
    [[:song/assoc-in song-id [:song/lyrics :lyrics/sections section-id]
      {:section/id section-id
       :section/title "Section Title"
       :section/lines [""]}]
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
                    conj section-id))

      :song/create
      (let [[song] params]
        (assoc songs-by-id (:song/id song) song))

       :song/update
       (let [[song-id attrs] params]
         (update songs-by-id song-id merge attrs))

       :song/delete
       (let [[song-id] params]
         (dissoc songs-by-id song-id))))
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
        [:xtdb.api/fn :song/append-section song-id section-id])

      :song/create
      (let [[song] params]
        (merge
         {:db/op :create
          :db/doc-type :song
          :xt/id (:song/id song)}
         song))

      :song/update
      (let [[song-id attrs] params]
        (merge
         {:db/op :update
          :db/doc-type :song
          :xt/id song-id}
         attrs))

      :song/delete
      (let [[song-id] params]
        {:db/op :delete
         :db/doc-type :song
         :xt/id song-id}))))