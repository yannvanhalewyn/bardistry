(ns bardistry.core
  (:require
   [reagent.core :as r]
   [react-native :as rn]
   [bardistry.db :as db]
   [promesa.core :as p]
   [applied-science.js-interop :as j]

   ))

;; (def component (.-default (js/require "../../foo.js")))
;; (def component (.-default (js/require "../../src/foo.js")))
(def component (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))

;; (def component (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))

(def view (r/adapt-react-class rn/View))
(def text (r/adapt-react-class rn/Text))

(defn app-root []
  (db/load-songs!)
  (fn []
    #_[view {:margin-top 64}
       [text {:style {:color "blue"}}
        "--I  change this and it just works! "]]

    [component {:songs (:songs @db/db)}]))

(defn ^:export -main
  []
  (r/as-element [app-root]))
