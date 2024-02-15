(ns bardistry.navigation
  (:require
   [applied-science.js-interop :as j]
   [bardistry.transit :as transit]))

(def rn-nav (js/require "@react-navigation/native"))
(def rn-elements (js/require "@react-navigation/elements"))

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

(def use-route (comp adapt-route (j/get rn-nav :useRoute)))
(def use-header-height (j/get rn-elements :useHeaderHeight))

(defn current-route []
  (adapt-route (ref-call :getCurrentRoute)))

(defn route-params [route]
  (:params route))
