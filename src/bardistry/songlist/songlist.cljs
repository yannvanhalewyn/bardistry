(ns bardistry.songlist.songlist
  (:require [reagent.core :as r]))

(def component
  (r/adapt-react-class
   (.-default (js/require "../../src/bardistry/songlist/SongList.js"))))
