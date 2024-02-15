(ns bardistry.rn.device-info
  (:require
   [promesa.core :as p]
   [applied-science.js-interop :as j]))

(def device-info (.-default (js/require "react-native-device-info")))

(def use-window-dimensions
  (j/get (js/require "react-native") :useWindowDimensions))

(defonce emulator?* (atom nil))

(defn emulator? []
  @emulator?*)

(-> (j/call device-info :isEmulator)
    (p/then #(reset! emulator?* %)))
