(ns bardistry.server.api
  (:require
   [com.biffweb :as biff]))

(defn- all-songs [{:keys [biff/db]}]
  {:status 200
   :body (biff/q db '{:find (pull song [:song/id :song/title :song/artist])
                      :where [[song :song/id]]})})

(defn- lyrics [{:keys [biff/db path-params] :as req}]
  (if-let [song (first
                 (biff/q db '{:find [(pull song [:song/id :song/contents])]
                              :in [id]
                              :where [[song :song/id id]]}
                         (parse-uuid (:id path-params))))]
    {:status 200
     :body song}
    {:status 404}))

(defn- query [{:keys [biff/db params]}]
  {:status 200
   :body (apply biff/q db (:query params) (:params params))})

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
   :api-routes [["/api/songs" {:get all-songs}]
                ["/api/songs/:id/lyrics" {:get lyrics}]
                ["/api/q" {:post query}]]})
