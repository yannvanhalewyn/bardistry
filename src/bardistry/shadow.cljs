(ns bardistry.shadow
  (:require
   [shadow.react-native :refer [render-root]]
   [reagent.core :as r]
   [bardistry.core :as core]))

(defn ^:dev/after-load init []
  (.log js/console "INIT, loading")
  (render-root "Bardistry" (r/as-element [core/app-root])))
