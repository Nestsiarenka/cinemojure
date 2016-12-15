(ns cinema.logic.util
  (:require [clojure.edn :as edn])
  (:import (java.util Date )
           (java.time Duration)))

(defn elem->list [map-value key]
  (if (not (sequential? (get map-value key)))
    (update map-value key list)
    map-value))

(defn now []
  (str (.getTime (new Date))))

(defn substract-dates [first-date second-date]
  (- (edn/read-string second-date) (edn/read-string first-date)))

(def substract-dates-minutes (comp #(/ % (* 1000 60))
                                   substract-dates))
