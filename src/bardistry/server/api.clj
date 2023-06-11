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
  {:song/id :uuid
   :section/id :uuid
   :song [:map {:closed true}
          [:xt/id :song/id]
          [:song/id :song/id]
          [:song/title :string]
          [:song/artist :string?]
          [:song/lyrics
           [:map {:closed true}
            [:lyrics/arrangement [:vector :section/id]]
            [:lyrics/sections
             [:vector
              [:map {:closed true}
               [:section/title :string]
               [:section/lines [:vector :string]]]]]]]]})

(def plugin
  {:schema schema
   :tx-fns {:section/set-lines
            '(fn [ctx song-id section-id new-lines]
               (when-let [song (xtdb.api/entity (xtdb.api/db ctx) song-id)]
                 (when (contains? (get-in song [:song/lyrics :lyrics/sections]) section-id)
                   [[:xtbd.api/put
                     (assoc-in
                      song
                      [:song/lyrics :lyrics/sections section-id :section/lines]
                      new-lines)]])))}
   :api-routes [["/api/q" {:post query}]
                ["/api/mutate" {:post mutate}]]})
