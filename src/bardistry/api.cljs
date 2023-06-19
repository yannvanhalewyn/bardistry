(ns bardistry.api
  (:require
   [applied-science.js-interop :as j]
   [bardistry.transit :as transit]
   [bardistry.rn.device-info :as device-info]
   [clojure.string :as str]
   [promesa.core :as p]))

(def production true)

(defn- transit-type? [content-type]
  (and (string? content-type)
       (str/starts-with? content-type "application/transit+json")))

(defn- content-type [res]
  (j/get-in res [:headers :map "content-type"]))

(defn get-dev-host []
  (if (device-info/emulator?)
    "localhost"
    "192.168.5.180"))

(def PORT 8080)

(defn- api-url [endpoint]
  (if production
    (str "https://bardistry.app/api/" endpoint)
    (str "http://" (get-dev-host) ":" PORT "/api/" endpoint)))

(defn request!
  [{::keys [endpoint method params on-success on-failure]}]
  (println "http.request" method endpoint params)
  (p/let [error-handler (fn [err]
                        (.error js/console "http.failure" (str method) endpoint err)
                        (on-failure err))
          res (-> (js/fetch (api-url endpoint)
                            (clj->js
                             {:method (or method :get)
                              :headers
                              {:Content-Type "application/transit+json"
                               :Accept "application/transit+json"}
                              :body (when params
                                      (transit/write params))}))
                  (p/catch error-handler))
          body (.text res)
          data (if (transit-type? (content-type res))
                 (transit/read body)
                 body)]
    (if (j/get res :ok)
      (do (println "http.success" (str method) endpoint (j/get res :status))
          (on-success data))
      (error-handler (clj->js data)))))

;; (defonce promise (atom nil))
;; (defonce response (atom nil))
;; (defonce result (atom nil))
;; (defonce error (atom nil))

(comment

  (let [params {:query '{:find (pull ?song [:song/id
                                            :song/title
                                            :song/artist])
                         :where [[?song :song/id _]]}}]
    (reset!
     promise
     (-> (js/fetch (api-url "q")
                   (clj->js
                    {:method "POST"
                     :headers {:Content-Type "application/transit+json"
                               :Accept "application/transit+json"}
                     :body (transit/write params)}))
         (p/then #(do
                    (reset! response %)
                    (.text %)))
         (p/then #(reset! result %))
         (p/catch #(reset! error %)))))

  @response

  @result

  @error

  @promise

  (request! {::endpoint "q"
             ::method :post
             ::params {:foo "bar3"}
             ::on-success #(reset! result %)
             ::on-failure #(reset! error %)})

  )
