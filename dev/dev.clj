(ns dev
  (:require
   [bardistry.core :as core]
   [bardistry.songlist.load-songs :as load-songs]
   [com.biffweb :as biff]
   [xtdb.api :as xt]))

(defn get-ctx []
  (biff/assoc-db @core/system))

(defn xt-node []
  (:biff.xtdb/node @core/system))

(defn q! [& args]
  (apply biff/q (xt/db (xt-node)) args))

(defn clear-db! [node]
  (xt/submit-tx
   node
   (for [id (com.biffweb/q (xt/db node) '{:find ?e :where [[?e :xt/id ?a]]})]
     [::xt/delete id])))

(def start #'core/start)
(def refresh #'core/refresh)

(comment
  (start)

  (refresh)

  (q! '{:find (pull ?song [:song/title :song/artist :song/lyrics])
        :limit 1
        :where [[?song :song/title "Mad World"]]})

  (clear-db! (xt-node))

  (load-songs/load-songs! (xt-node))

  )
