(ns bardistry.song)

(defn make []
  {:song/id (random-uuid)
   :song/title "New Title"
   :song/artist "New Artist"
   :song/sort-artist "Artist"
   :song/contents ["line 1" "line 2"]})
