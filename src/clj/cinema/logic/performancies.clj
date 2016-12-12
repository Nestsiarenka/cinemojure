(ns cinema.logic.performancies
  (:require  [cinema.models.film :refer :all]
             [cinema.models.session :refer :all]
             [cinema.percistance.film :as percistance-film]
             [cinema.percistance.session :as percistance-session]))

(defn map-with-sessions
  [film]
  (->> film
      (percistance-session/get-sessions-by-film)
      (assoc film :sessions)))

(defn get-films
  []
  (map
   map-with-sessions
   (percistance-film/get-films)))

(defn get-session-by-id
  [id]
  (percistance-session/get-session-by-id (read-string id)))

