(ns bardistry.server.api
  (:require
   [com.biffweb :as biff]))

(defn echo [{:keys [params biff/db]}]
  (biff/q db '{:find [e title] :where [[e :song/title title]]})
  {:status 200
   :body params})

(def plugin
  {:api-routes [["/api/echo" {:post echo}]]})
