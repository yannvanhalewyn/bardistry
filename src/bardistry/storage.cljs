(ns bardistry.storage
  (:require
   [bardistry.transit :as transit]
   ["@react-native-async-storage/async-storage$default" :as async-storage]
   [promesa.core :as p]))

(defonce result (atom nil))

(defn store [k v]
  (-> (async-storage/setItem k v)
      (p/catch #(.error js/console %))))

(defn retreive [k]
  (-> (async-storage/getItem k)
      (p/catch #(.error js/console %))))

(defn store-clj [k v]
  (store k (transit/write v)))

(defn retreive-clj [k]
  (p/let [value (retreive k)]
    (transit/read value)))

(comment

  (store "foo" "some other value")

  (async-storage/clear)
  (store-clj "bardistry.songs" [:foo :bar])

  (p/let [value (retreive "bardistry.songs")]
    (.log js/console "value:" value))

  (p/let [value (retreive-clj "bardistry.songs")]
    (reset! result value))

  )
