(ns bardistry.http
  (:require
   [bardistry.transit :as transit]
   [promesa.core :as p]))

(def HOST "192.168.5.180")
(def PORT 8080)

(defn- api-url [endpoint]
  (str "http://" HOST ":" PORT "/" endpoint))

(defn load! [{::keys [endpoint on-success on-failure]}]
  (.log js/console "fetching" endpoint)
  (-> (p/let [res (js/fetch (api-url endpoint))
              body (.text res)
              data (transit/read body)]
        (.log js/console "SUCCESS" endpoint)
        (on-success data))
      (p/catch
          (fn [err]
            (.error js/console "ERROR" endpoint err)
            (on-failure err)))))
