(ns dev
  (:require
   [bardistry.core :as core]
   [bardistry.songlist.tx :as songlist.tx]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [clojure.tools.namespace.repl :as tools.ns.repl]
   [com.biffweb :as biff]
   [com.biffweb.impl.xtdb :as biff.xt]
   [dev.seed.load-songs :as load-songs]
   [malli.core :as malli]
   [xtdb.api :as xt]))

(defn get-ctx []
  (biff/assoc-db @core/system))

(defn get-node []
  (:biff.xtdb/node @core/system))

(defn get-db []
  (:biff/db (get-ctx)))

(defn q! [& args]
  (apply biff/q (get-db) args))

(defn submit-tx [tx]
  (biff/submit-tx (get-ctx) tx))

(defn with-tx [tx]
  (xt/with-tx (get-db) tx))

(defn biff-tx->xt [tx]
  (biff.xt/biff-tx->xt (get-ctx) tx))

(defn lookup [k v]
  (biff/lookup (get-db) k v))

(defn clear-db! []
  (xt/submit-tx
   (get-node)
   (for [id (com.biffweb/q (get-db) '{:find ?e :where [[?e :xt/id ?a]]})]
     [::xt/delete id])))

(def start #'core/start)

(defn refresh []
  (doseq [f (:biff/stop @core/system)]
    (log/info "stopping:" (str f))
    (f))
  (tools.ns.repl/refresh :after `start))

(defn reset []
  (doseq [f (:biff/stop @core/system)]
    (log/info "stopping:" (str f))
    (f))
  (start))

(defn valid? [doc-type doc]
  (malli/validate doc-type doc @(:biff/malli-opts @core/system)))

(defn explain [doc-type doc]
  (malli/explain doc-type doc @(:biff/malli-opts @core/system)))

(comment
  (set! *print-namespace-maps* false)

  ;; System / DB management
  (start)

  (refresh)

  (clear-db!)

  (load-songs/load-songs! (get-node))

  ;; Song model and schema
  (def new-song (bardistry.song/make))
  (def db-song (lookup :song/title "Karma Police"))

  (valid? :song new-song)
  (valid? :song db-song)
  (explain :song db-song)

  ;; Setting Tags
  (def nina-songs ["Lost on You"
                   "Cherry Wine"
                   "Aerials"
                   "You Don't Get Me High Anymore"
                   "Shallow"
                   "Little Lion Man"
                   "Awake my Soul"
                   "Sound of Silence"
                   "In a Week"
                   "Wish You were Here"
                   "My Hero"
                   "Exit Music"])

  (biff/submit-tx
   (get-ctx)
   (for [song-title nina-songs]
     (do (when-not (lookup :song/title song-title)
           (println "Not found" song-title))
         {:db/doc-type :song
          :db.op/upsert {:song/title song-title}
          :song/tags #{"Nina"}})))

  ;; Playground

  (biff-tx->xt
   [{:db/op :merge
     :db/doc-type :song
     :xt/id song-id
     :song/title "New title"}])

  (biff-tx->xt
   [{:db/op :update
     :db/doc-type :song
     :xt/id song-id
     :song/title "Karma Police 2"}])

  (doseq [song-title nina-songs]
    (let [song (lookup :song/title song-title)]
      (when-not (lookup :song/title song-title)
        (println "Not found" song-title))
      (println song-title (:song/tags song))))

  (def song-id #uuid "aaecace1-1349-4b3f-ba80-51dc5b7954d6")
  (def section-id #uuid "42b24b16-e4d2-4fc6-b15b-15debbbfd268")
  (def new-lines ["Chorus"
                  "Let it be, let it be"
                  "Let it be, let it be"
                  "Whisper words of wisdom, let it"
                  "edited 2"])

  (-> (lookup :song/id song-id)
      (get-in [:song/lyrics :lyrics/sections section-id]))

  (def new-song (bardistry.song/make))
  (def song-id (:song/id new-song))
  (xt/submit-tx (get-node)
                [[::xt/put (assoc new-song :xt/id (:song/id new-song))]])

  (def song-id (:song/id (lookup :song/title "")))

  (submit-tx [{:db/op :delete
               :xt/id song-id}])

  (xt/submit-tx (get-node)
                (->> (songlist.tx/append-section song-id)
                     (songlist.tx/mutations->tx)
                     #_(take 1)))

  (biff/lookup (:biff/db (get-ctx)) :song/id song-id)

  (lookup :song/title "Test")

  (submit-tx [[:xtdb.api/fn
               :song/assoc-in #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
               [:song/lyrics :lyrics/sections #uuid "69595e3d-9d02-4303-b344-af4216ce9120" :section/highlight?]
               false]])

  (let [song (lookup :song/title "Let it Be")
        section-id (first (keys (get-in song [:song/lyrics :lyrics/sections])))]
    (submit-tx [[:xtdb.api/fn
                 :song/dissoc-in (:song/id song)
                 [:song/lyrics :lyrics/sections section-id]]
                [:xtdb.api/fn
                 :song/remove-section (:song/id song) section-id]]))

  (lookup :xt/id #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37")

  (submit-tx
   [[:xtdb.api/fn
     :song/dissoc-in #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     [:song/lyrics :lyrics/sections #uuid "6c387cca-5d9a-4535-96bc-fbd47fa47729"]]
    [:xtdb.api/fn
     :song/remove-section
     #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37" #uuid "6c387cca-5d9a-4535-96bc-fbd47fa47729"]])

  (submit-tx
   [[:xtdb.api/fn :song/assoc-in #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     [:song/lyrics :lyrics/sections #uuid "59970524-12b3-4175-98fe-e8adc70f401b"]
     {:section/id #uuid "59970524-12b3-4175-98fe-e8adc70f401b",
      :section/title "Section Title",
      :section/lines []}]
    [:xtdb.api/fn :song/append-section #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37" #uuid "59970524-12b3-4175-98fe-e8adc70f401b"]])

  (submit-tx
   [[:xtdb.api/fn :song/assoc-in #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     [:song/lyrics :lyrics/sections #uuid "1bea4b3d-58ea-425d-a2d3-2b0e1b067242"]
     {:section/id #uuid "1bea4b3d-58ea-425d-a2d3-2b0e1b067242", :section/title "Section Title", :section/lines [""]}]
    [:xtdb.api/fn :song/append-section #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     #uuid "1bea4b3d-58ea-425d-a2d3-2b0e1b067242"]])

  (submit-tx
   [[:xtdb.api/fn
     :song/dissoc-in
     #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     [:song/lyrics :lyrics/sections #uuid "59970524-12b3-4175-98fe-e8adc70f401b"]]
    [:xtdb.api/fn
     :song/remove-section
     #uuid "7cfebf6a-245b-46db-aa83-caa13fcf0f37"
     #uuid "59970524-12b3-4175-98fe-e8adc70f401b"]]))
