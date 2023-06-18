(ns bardistry.tasks
  (:require
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
                (str/join " ")
                (str "clj "))))
