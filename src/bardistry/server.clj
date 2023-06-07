(ns bardistry.server
  (:require
   [bardistry.songs :as songs]
   [bardistry.transit :as transit]
   [clojure.java.io :as io]
   [org.httpkit.server :as http]))

(defn app [req]
  {:status 200
   :headers {"Content-Type" "application/transit"}
   :body (transit/write (songs/read-songs! (io/resource "lyrics.txt")))})

(defonce server (atom nil))

(defn stop-server []
  (if @server
    (reset! server (@server))
    (println "Server not running")))

(defn start-server []
  (if @server
    (println "Server already running")
    (do (reset! server (http/run-server app {:port 8080}))
        (println "Server started on port 8080"))))

(defn restart-server []
  (stop-server)
  (start-server))

(comment

  (start-server)

  (stop-server)

  (restart-server)

  )
