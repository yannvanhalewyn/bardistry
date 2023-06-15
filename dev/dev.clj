(ns dev
  (:require
   [bardistry.core :as core]
   [bardistry.songlist.tx :as songlist.tx]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [clojure.tools.namespace.repl :as tools.ns.repl]
   [com.biffweb :as biff]
   [com.biffweb.impl.xtdb :as biff.xt]
   [dev.seed.load-songs :as load-songs]
   [malli.core :as malli]
   [xtdb.api :as xt]))

(defn get-ctx []
  (biff/assoc-db @core/system))

(defn get-node []
  (:biff.xtdb/node @core/system))

(defn get-db []
  (:biff/db (get-ctx)))

(defn q! [& args]
  (apply biff/q (get-db) args))

(defn submit-tx [tx]
  (biff/submit-tx (get-ctx) tx))

(defn with-tx [tx]
  (xt/with-tx (get-db) tx))

(defn biff-tx->xt [tx]
  (biff.xt/biff-tx->xt (get-ctx) tx))

(defn lookup [k v]
  (biff/lookup (get-db) k v))

(defn clear-db! []
  (xt/submit-tx
   (get-node)
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

  (load-songs/load-songs! (get-node))

  ;; Song model and schema
  (def new-song (bardistry.song/make))
  (def db-song (lookup :song/title "Karma Police"))

  (valid? :song new-song)
  (valid? :song db-song)
  (explain :song db-song)

  ;; Setting Tags
  (def nina-songs ["Lost on You"
                   "Cherry Wine"
                   "Aerials"
                   "You Don't Get Me High Anymore"
                   "Shallow"
                   "Little Lion Man"
                   "Awake my Soul"
                   "Sound of Silence"
                   "In a Week"
                   "Wish You were Here"
                   "My Hero"
                   "Exit Music"])

  (biff/submit-tx
   (get-ctx)
   (for [song-title nina-songs]
     (do (when-not (lookup :song/title song-title)
           (println "Not found" song-title))
         {:db/doc-type :song
          :db.op/upsert {:song/title song-title}
          :song/tags #{"Nina"}})))
)
