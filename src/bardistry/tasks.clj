(ns bardistry.tasks
  (:require
   [babashka.tasks :as bb-tasks]
   [clojure.edn :as edn]
   [clojure.string :as str]))

(def config
  (delay (:tasks (edn/read-string (slurp "config.edn")))))

(defn run-args []
  (:biff.tasks/clj-args @config))

(defn shell-escape [s]
  (str \' (some-> s (str/replace "'" "'\"'\"'")) \'))

(defn run-cmd []
  (println "clojure"
           (->> (run-args)
                (map shell-escape)
                (str/join " "))))

(defn prod-repl
  "Opens an SSH tunnel so you can connect to the server via nREPL."
  []
  (println "Connect to nrepl port 7888")
  (spit ".nrepl-port" "7888")
  (bb-tasks/shell "ssh" "-NL" "7888:localhost:42277" (str "root@" (:biff.tasks/server @config)))
  )

(defn query-api [& args]
  ;; cat params.edn | jet --from edn --to transit | curl -XPOST --data @'-' --header 'Content-Type: application/transit+json' https://bardistry.app/api/q

  )


;; Run this over production repl to backup songs and then download files
(comment
  (com.biffweb/q (xtdb.api/db (:biff.xtdb/node @bardistry.core/system))
                 '{:find [?artist ?title]
                   :where [[?e :song/artist ?artist]
                           [?e :song/title ?title]]
                   :order-by [[?artist :asc]]})


  )
