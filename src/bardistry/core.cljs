(ns bardistry.core
  (:require
   [applied-science.js-interop :as j]
   [bardistry.db :as db]
   [bardistry.navigation :as nav]
   [bardistry.songlist.songlist :as songlist]
   [bardistry.songlist.lyrics :as lyrics]
   [reagent.core :as r]))

(def App
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/App.js"))))

(defn app-root []
  (db/hydrate!)
  (db/load-songs!)
  (fn []
    [App {:navigation-ref nav/navigation-ref
          :onEditPress lyrics/toggle-form!
          :screens
          [{:name "Songs"
            :component
            #(r/as-element [:f> songlist/component])}
           {:name "Lyrics"
            :component
            (fn [props]
              (let [route (nav/adapt-route (j/get props :route))]
                (r/as-element [lyrics/component (nav/route-params route)])))}]}]))

(defn ^:export -main
  []
  (r/as-element [app-root]))
