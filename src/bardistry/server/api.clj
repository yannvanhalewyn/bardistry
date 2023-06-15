(ns bardistry.server.api
  (:require
   [bardistry.songlist.tx :as songlist.tx]
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
   :song/tags   [:set :string]
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
   :section/id         :uuid
   :section/title      [:maybe :string]
   :section/lines      [:vector :string]
   :section/highlight? [:boolean]
   :section            [:map {:closed true}
                        :section/id
                        :section/title
                        :section/lines
                        :section/highlight?]})

(def plugin
  {:schema schema
   :tx-fns songlist.tx/tx-fns
   :api-routes [["/api/q" {:post query}]
                ["/api/mutate" {:post mutate}]]})
