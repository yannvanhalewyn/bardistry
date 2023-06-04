(ns bardistry.core
  (:require
   [reagent.core :as r]
   [react-native :as rn]
   [bardistry.db :as db]
   [clojure.string :as str]))

;; (def component (.-default (js/require "../../foo.js")))
;; (def component (.-default (js/require "../../src/foo.js")))
(def component (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))
(def App (r/adapt-react-class (.-default (js/require "../../src/bardistry/App.js"))))

;; (def component (r/adapt-react-class (.-default (js/require "../../src/bardistry/Component.js"))))

(def view (r/adapt-react-class rn/View))
(def text (r/adapt-react-class rn/Text))

(defn app-root []
  (db/load-songs!)
  (fn []
    #_[view {:margin-top 64}
       [text {:style {:color "blue"}}
        "--I  change this and it just works! "]]

    [App {:songs (for [song (:songs @db/db)]
                         (update song :song/contents #(str/trim (str/join "\n" %))))}]))

(defn ^:export -main
  []
  (r/as-element [app-root]))
