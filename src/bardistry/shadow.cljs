(ns bardistry.shadow
  (:require
   [shadow.react-native :refer [render-root]]
   [reagent.core :as r]
   [bardistry.core :as core]))

(defn ^:dev/after-load init []
  (render-root "Bardistry" (r/as-element [core/app-root])))
