(ns bardistry.transit
  (:refer-clojure :exclude [read])
  (:require [cognitect.transit :as transit])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))

(defn write
  ([x]
   (let [out (ByteArrayOutputStream.)]
     (write out x)
     (.toString out)))
  ([out x]
   (transit/write (transit/writer out :json-verbose) x)))

(defn read [in]
  (transit/read (transit/reader (ByteArrayInputStream. in) :json-verbose)))

(comment
  (write {:a ["b" "c"]})

  )
