(ns cinema.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[cinema started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[cinema has shut down successfully]=-"))
   :middleware identity})
