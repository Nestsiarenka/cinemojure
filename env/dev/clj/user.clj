(ns user
  (:require [mount.core :as mount]
            cinema.core))

(defn start []
  (mount/start-without #'cinema.core/repl-server))

(defn stop []
  (mount/stop-except #'cinema.core/repl-server))

(defn restart []
  (stop)
  (start))


