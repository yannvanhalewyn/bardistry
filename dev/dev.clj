(ns dev
  (:require
   [bardistry.core :as core]
   [bardistry.songlist.load-songs :as load-songs]
   [clojure.tools.logging :as log]
   [clojure.tools.namespace.repl :as tools.ns.repl]
   [com.biffweb :as biff]
   [xtdb.api :as xt]
   [malli.core :as malli]))

(defn get-ctx []
  (biff/assoc-db @core/system))

(defn xt-node []
  (:biff.xtdb/node @core/system))

(defn get-db []
  (xt/db (xt-node)))

(defn q! [& args]
  (apply biff/q (get-db) args))

(defn lookup [k v]
  (biff/lookup (get-db) k v))

(defn clear-db! []
  (xt/submit-tx
   (xt-node)
   (for [id (com.biffweb/q (get-db) '{:find ?e :where [[?e :xt/id ?a]]})]
     [::xt/delete id])))

(def start #'core/start)

(defn refresh []
  (doseq [f (:biff/stop @core/system)]
    (log/info "stopping:" (str f))
    (f))
  (tools.ns.repl/refresh :after `start))

(defn reset []
  (doseq [f (:biff/stop @core/system)]
    (log/info "stopping:" (str f))
    (f))
  (start))

(defn valid? [doc-type doc]
  (malli/validate doc-type doc @(:biff/malli-opts @core/system)))

(defn explain [doc-type doc]
  (malli/explain doc-type doc @(:biff/malli-opts @core/system)))

(comment
  (set! *print-namespace-maps* false)

  ;; System / DB management
  (start)

  (refresh)

  (clear-db!)

  (load-songs/load-songs! (xt-node))

  ;; Song model and schema
  (def new-song (bardistry.song/make))
  (def db-song (lookup :song/title "Karma Police"))

  (valid? :song new-song)
  (valid? :song db-song)
  (explain :song db-song)

)
