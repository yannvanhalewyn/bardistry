(ns bardistry.transit
  (:require [cognitect.transit :as t]))

(defn read [x]
  (t/read (t/reader :json-verbose) x))

(defn write [x]
  (t/write (t/writer :json-verbose) x))
