(ns bardistry.core
  (:require
   [reagent.core :as r]
   [react-native :as rn]))

(.log js/console "core.cljs")

;; (def component (r/adapt-react-class (.-default (js/require "../src/bardistry/Component.js"))))

(def view (r/adapt-react-class rn/View))
(def text (r/adapt-react-class rn/Text))

(defn app-root
  []
  [view {:margin-top 64}
   [text {:style {:color "blue"}}
    "Testing works"]]

  #_[component {:songs (:songs @db/db)
              :onPress #(swap! @db/db update :count inc)}])

(defn ^:export -main
  []
  (r/as-element [app-root]))
