(ns cinema.percistance.seat
  (:require [cinema.db.core :as db]
   :require [cinema.models.seat :refer :all]))

(defn validate-update [seat]
  (let [current-seat-status
        (:seat_status (db/get-sessions-seat seat))
        wished-seat-status
        (:seat_status seat)]
    (case current-seat-status
      "free" (case wished-seat-status
               "incart" true
               "blocked" true
               false)
      "booked" (case wished-seat-status
                 "blocked" true
                 false) 
      "blocked" (case wished-seat-status
                  "free" true
                  false)
      true)))

(defn update-seats-mapping
  [value]
  (when (> (db/update-seats! (update value :seat_status
                                           db/keyword->pg-enum "seat_status")) 0)
    (map->Seat (db/get-sessions-seat value)))
  )

(defn update-seats-mapping-validated
  [value]
  (if (validate-update value)
    (update-seats-mapping value)
    nil))

(defn update-seats! [seats]
 (map update-seats-mapping seats))

(defn update-seats-validated! [seats]
  (map update-seats-mapping-validated seats))
