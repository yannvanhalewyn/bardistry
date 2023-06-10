(ns dev
  (:require
   [bardistry.core :as core]
   [bardistry.songlist.load-songs :as load-songs]
   [com.biffweb :as biff]
   [xtdb.api :as xt]))

(set! *print-namespace-maps* false)

(defn get-ctx []
  (biff/assoc-db @core/system))

(defn xt-node []
  (:biff.xtdb/node @core/system))

(defn get-db []
  (xt/db (xt-node)))

(defn q! [& args]
  (apply biff/q (get-db) args))

(defn clear-db! []
  (xt/submit-tx
   (xt-node)
   (for [id (com.biffweb/q (get-db) '{:find ?e :where [[?e :xt/id ?a]]})]
     [::xt/delete id])))

(def start #'core/start)
(def refresh #'core/refresh)

(comment
  (start)

  (refresh)

  (q! '{:find (pull ?song [:song/title :song/artist :song/lyrics])
        :limit 1
        :where [[?song :song/title "When You Break"]]})

  (clear-db! (xt-node))

  (load-songs/load-songs! (xt-node))

  )
