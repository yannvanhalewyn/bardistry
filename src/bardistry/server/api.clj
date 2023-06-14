(ns bardistry.server.api
  (:require
   [com.biffweb :as biff]))

(defn- query [{:keys [biff/db params]}]
  {:status 200
   :body (apply biff/q db (:query params) (:params params))})

(defn- mutate [{:keys [params] :as ctx}]
  {:status 200
   :body (biff/submit-tx ctx (:db/tx-ops params))})

(def schema
  {;; Song
   :song/id     :uuid
   :song/title  :string
   :song/artist :string
   :song/tags [:set :string]
   :song        [:map {:closed true}
                 [:xt/id {:optional true} :uuid]
                 :song/id
                 :song/title
                 :song/artist
                 [:song/tags {:optional true}]
                 [:song/lyrics :lyrics]]

   ;; Lyrics
   :lyrics/arrangement [:vector :section/id]
   :lyrics/sections    [:map-of :section/id :section]
   :lyrics             [:map {:closed true}
                        :lyrics/arrangement
                        :lyrics/sections]

   ;; Section
   :section/id    :uuid
   :section/title [:maybe :string]
   :section/lines [:vector :string]
   :section       [:map {:closed true}
                   :section/id
                   :section/title
                   :section/lines]})

(def plugin
  {:schema schema
   :tx-fns {:section/set-lines
            '(fn [ctx song-id section-id new-lines]
               (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
                 (when (contains? (get-in song [:song/lyrics :lyrics/sections]) section-id)
                   ;; TODO compare old and new and reject if not the same
                   [[:xtdb.api/put
                     (assoc-in
                      song
                      [:song/lyrics :lyrics/sections section-id :section/lines]
                      new-lines)]])))}
   :api-routes [["/api/q" {:post query}]
                ["/api/mutate" {:post mutate}]]})
