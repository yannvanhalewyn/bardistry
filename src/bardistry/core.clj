(ns bardistry.core
  (:require [com.biffweb :as biff]
            [bardistry.server.api :as api]
            [bardistry.server.email :as email]
            [bardistry.server.worker :as worker]
            [clojure.test :as test]
            [clojure.tools.logging :as log]
            [malli.core :as malc]
            [malli.registry :as malr]))

(def plugins
  [(biff/authentication-plugin {})
   api/plugin
   worker/plugin])

(def routes [["" {:middleware [biff/wrap-site-defaults]}
              (keep :routes plugins)]
             ["" {:middleware [biff/wrap-api-defaults]}
              (keep :api-routes plugins)]])

(def handler (-> (biff/reitit-handler {:routes routes})
                 biff/wrap-base-defaults))

(defn on-save [ctx]
  (biff/add-libs)
  (biff/eval-files! ctx)
  (test/run-all-tests #"bardistry.*"))

(def malli-opts
  {:registry (malr/composite-registry
              malc/default-registry
              (apply biff/safe-merge
                     (keep :schema plugins)))})

(def initial-system
  {:biff/plugins #'plugins
   :biff/send-email #'email/send-email
   :biff/handler #'handler
   :biff/malli-opts #'malli-opts
   :biff.beholder/on-save #'on-save
   :biff.xtdb/tx-fns biff/tx-fns})

(defonce system (atom {}))

(def get-secret
  (let [secrets (delay (read-string (slurp "secrets.edn")))]
    (fn [ctx k]
      (get @secrets (get ctx k)))))

(defn use-secrets [ctx]
  (when-not (every? #(get-secret ctx %) [:biff.middleware/cookie-secret :biff/jwt-secret])
    (binding [*out* *err*]
      (println "Secrets are missing, add a secrets.edn")))
  (assoc ctx :biff/secret #(get-secret ctx %)))

(def components
  [biff/use-config
   use-secrets
   biff/use-xt
   biff/use-queues
   biff/use-tx-listener
   biff/use-jetty
   biff/use-chime
   biff/use-beholder])

(defn start []
  (let [new-system (reduce (fn [system component]
                             (log/info "starting:" (str component))
                             (component system))
                           initial-system
                           components)]
    (reset! system new-system)
    (log/info "Go to" (:biff/base-url new-system))))
