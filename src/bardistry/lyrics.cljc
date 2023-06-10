(ns bardistry.lyrics
  (:require
   [medley.core :as m]))

(defn make []
  (let [section-id (random-uuid)]
    {:lyrics/sections
     {section-id {:section/title "Verse"
                  :section/lines [""]}}
     :lyrics/arrangement [section-id]}))
