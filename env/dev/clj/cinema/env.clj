(ns cinema.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [cinema.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[cinema started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[cinema has shut down successfully]=-"))
   :middleware wrap-dev})
