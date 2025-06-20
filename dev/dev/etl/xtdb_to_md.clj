(ns dev.etl.xtdb-to-md
  "Extracts songs from XTDB and transforms them into markdown files structured
  by <artist>/<song>.md"
  (:require
    [bardistry.lyrics :as lyrics]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [com.biffweb :as biff]
    [clojure.tools.logging :as log]))

(defn song->md [{:keys [song/title song/artist song/lyrics]}]
  (let [sb (StringBuilder.)]
    (.append sb (str "---\nid: " title
                     "\naliases: []"
                     "\ntags: []"
                     "\nartist: " artist
                     "\ntitle: " title
                     "\n---\n"))
    (doseq [{:keys [section/title section/lines]} (lyrics/sections lyrics)]
      (.append sb \newline)
      (.append sb (str "[" title "]\n"))
      (.append sb (str/join "\n" lines))
      (.append sb \newline))
    (String. sb)))

(defn song-subpath [song]
  (str (:song/artist song) "/" (:song/title song) ".md"))

(defn target-file-path [song]
  (str "target/md-songbook/" (song-subpath song)))

(defn process! [songs]
  (doseq [song songs]
    (let [filepath (target-file-path song)
          file (io/file filepath)]
      (io/make-parents file)
      (spit file (song->md song)))))

(defn copy-to-obsidian-vault!
  [{:keys [obsidian-vault-dir local-songs]}]
  (doseq [song local-songs]
    (let [obsidian-path (str obsidian-vault-dir
                            (song-subpath song))]

      (when (.exists (io/file obsidian-path))
        (log/info "Deleting" obsidian-path)
        (io/delete-file obsidian-path))

      (log/info "Copying" (song-subpath song))
      (io/copy
        (io/file (target-file-path song))
        (io/file obsidian-path)))))

(comment
  (def local-songs
    (biff/q (dev/get-db)
      '{:find (pull ?e [*])
        :where [[?e :song/title ?a]]}))

  (process! local-songs)
  (copy-to-obsidian-vault!
    {:obsidian-vault-dir "/Users/yannvanhalewyn/Library/Mobile Documents/iCloud~md~obsidian/Documents/Music/Lyrics/"
     :local-songs local-songs})

  (file-seq (io/file "/Users/yannvanhalewyn/Library/Mobile Documents/iCloud~md~obsidian/Documents/Music/Lyrics/"))

  (spit ",test.md" (song->md (first local-songs))))
