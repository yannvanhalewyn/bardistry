(ns bardistry.navigation
  (:require
   [applied-science.js-interop :as j]
   [bardistry.transit :as transit]))

(defonce nav (atom nil))

(defn- ref-call [f & args]
  (when-let [n @nav]
    (apply j/call n f args)))

(defn navigation-ref [ref]
  (reset! nav ref))

(defn navigate!
  ([route]
   (navigate! route {}))
  ([route params]
   (ref-call :navigate route (transit/write params))))

(defn adapt-route [rn-route]
  (cond-> rn-route
    (string? (j/get rn-route :params))
    (j/update! :params transit/read)
    rn-route (js->clj :keywordize-keys true)))

(defn current-route []
  (adapt-route (ref-call :getCurrentRoute)))

(defn route-params [route]
  (:params route))
