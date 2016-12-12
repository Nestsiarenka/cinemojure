(ns cinema.logic.booking
  (:require [cinema.percistance.seat :as percistance-seat]
            [cinema.models.seat :refer :all]
            [cinema.logic.util :as util]
            [clojure.edn :as edn]))

(def email-sender (agent '(percistance-seat)))

(defn seats_ids->seats [params status]
  (fn [seat_id]
    (map->Seat {:seat_id (edn/read-string seat_id)
                :session_id (edn/read-string (:session_id params))
                :user_id (get-in params [:user :id])
                :seat_status status})))

(defn book-seats! [params]
  (let [params (util/elem->list params :seats_ids)]
    (filter some?
            (-> (map (seats_ids->seats params "booked") (:seats_ids params))
                (percistance-seat/update-seats-validated!)))))
