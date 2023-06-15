(ns bardistry.lyrics
  (:require
   [medley.core :as m]))

(defn make []
  (let [section-id (random-uuid)]
    {:lyrics/sections
     {section-id {:section/id section-id
                  :section/title "Verse"
                  :section/lines [""]
                  :section/highlight? false}}
     :lyrics/arrangement [section-id]}))

(defn sections [{:lyrics/keys [sections arrangement]}]
  (map sections arrangement))
