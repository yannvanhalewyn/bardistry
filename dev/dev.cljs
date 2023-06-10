(ns dev
  (:require [bardistry.db :as db]))

(set! *print-namespace-maps* false)

(def db db/db)
