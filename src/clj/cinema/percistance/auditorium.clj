(ns cinema.percistance.auditorium
  (:require [cinema.db.core :as db]
            [cinema.models.auditorium :refer :all]))

(defn get-auditoriums []
  (map map->Auditorium (db/get-auditoriums)))
