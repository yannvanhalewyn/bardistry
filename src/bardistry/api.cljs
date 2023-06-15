(ns bardistry.api
  (:require
   [applied-science.js-interop :as j]
   [bardistry.transit :as transit]
   [bardistry.rn.device-info :as device-info]
   [clojure.string :as str]
   [promesa.core :as p]))

(defn- transit-type? [content-type]
  (and (string? content-type)
       (str/starts-with? content-type "application/transit+json")))

(defn- content-type [res]
  (j/get-in res [:headers :map "content-type"]))

(defn get-host []
  (if (device-info/emulator?)
    "localhost"
    "192.168.5.180"))

(def PORT 8080)

(defn- api-url [endpoint]
  (str "http://" (get-host) ":" PORT "/api/" endpoint))

(defn request! [{::keys [:bardistry.api/endpoint :bardistry.api/method :bardistry.api/params :bardistry.api/on-success :bardistry.api/on-failure]}]
  (println "http.request" method endpoint params)
  (p/let [res (-> (js/fetch (api-url endpoint)
                            (clj->js
                             {:method (or method :get)
                              :headers
                              {:Content-Type "application/transit+json"
                               :Accept "application/transit+json"}
                              :body (when params
                                      (transit/write params))}))
                  (p/catch
                      (fn [err]
                        (.error js/console "http.failure" (str method) endpoint err)
                        (on-failure err))))
          body (.text res)
          data (if (transit-type? (content-type res))
                 (transit/read body)
                 body)]
    (println "http.success" (str method) endpoint)
    (on-success data)))

;; (defonce response (atom nil))
;; (defonce result (atom nil))
;; (defonce error (atom nil))

(comment

  (-> (js/fetch (api-url "songs")
                (clj->js
                 {:headers {:Accept "application/transit+json"}})
                ;; #js {:method "POST"
                ;;      :headers {:Content-Type "application/transit+json"
                ;;                :Accept "application/transit+json"}}

                )
      (p/then #(do
                 (reset! response %)
                 (.text %)))
      (p/then #(reset! result %))
      (p/catch #(reset! error %)))

  @result

  @error

  (request! {::endpoint "q"
             ::method :post
             ::params {:foo "bar3"}
             ::on-success #(reset! result %)
             ::on-failure #(reset! error %)})

  )
