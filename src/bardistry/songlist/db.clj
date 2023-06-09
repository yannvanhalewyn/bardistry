(ns baridstry.songlist.db
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(def xtdb-node (start-xtdb!))
;; note that attempting to eval this expression more than once before first
;; calling `stop-xtdb!` will throw a RocksDB locking error this is because a
;; node that depends on native libraries must be `.close`'d explicitly

(defn stop-xtdb! []
  (.close xtdb-node))

(comment
  (xt/submit-tx xtdb-node [[::xt/put
                            {:xt/id "some-id"
                             :user/name "Yann"}]])

  (xt/q (xt/db xtdb-node) '{:find [e]
                            :where [[e :user/name "Yann"]]} )

  )
