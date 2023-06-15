(ns bardistry.rn.device-info
  (:require
   [promesa.core :as p]
   [applied-science.js-interop :as j]))

(def device-info (.-default (js/require "react-native-device-info")))

(defonce emulator?* (atom nil))

(defn emulator? []
  @emulator?*)

(-> (j/call device-info :isEmulator)
    (p/then #(reset! emulator?* %)))
