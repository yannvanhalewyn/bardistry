(ns bardistry.server.api
  (:require
   [com.biffweb :as biff]
   [xtdb.api :as xt]))

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

(def plugin
  {:api-routes [["/api/songs" {:get all-songs}]
                ["/api/songs/:id/lyrics" {:get lyrics}]]})
