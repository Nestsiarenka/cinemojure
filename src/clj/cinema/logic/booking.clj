(ns cinema.logic.booking
  (:require [cinema.percistance.seat :as seat]))

(def incart-seats (agent '()))

(defn make-seat-free
  [seat]
  (seat/update-seats-validated! [(assoc seat
                                        :seat_status "free")]))

(defn compare-seats
  [seat1 seat2]
  (= (select-keys seat1 [:seat_id :session_id])
     (select-keys seat2 [:seat_id :session_id])))

(defn delete-from-incart-seats [seat]
  (send incart-seats
        (filter (fn [value]
                  (not (compare-seats seat value))))))

(defn incart-seat-watcher
  [key watched old-state new-state]
  (let [counter (:counter new-state)]
    (when (>= counter 5)
      (make-seat-free new-state))))

(defn add-to-incart-seats [seat]
  (let [new-incart-seat (atom (assoc seat :couner 0))]
    (add-watch new-incart-seat :incart-seat-watcher
               incart-seat-watcher)))

(defn incart-seats! [seats]
  (let [updated-seats (seat/update-seats-validated! seats)]
    (->> updated-seats
         (filter (fn [value] (=
                              (:seat_status value)
                              "incart")))
         (apply add-to-incart-seats))
    updated-seats))

