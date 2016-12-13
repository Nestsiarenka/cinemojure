(ns cinema.logic.booking
  (:require [cinema.percistance.seat :as percistance-seat]
            [cinema.models.seat :refer :all]
            [cinema.logic.util :as util]
            [clojure.edn :as edn]
            [cinema.services.smtp :refer :all]))

(def email-sender (agent (->Smtp)))

(defn seats_ids->seats [params status]
  (fn [seat_id]
    (map->Seat {:seat_id (edn/read-string seat_id)
                :session_id (edn/read-string (:session_id params))
                :user_id (get-in params [:user :id])
                :seat_status status})))

(defn book-seats! [params]
  (let [params (util/elem->list params :seats_ids)]
    (let [booked-tickets
          (filter some?
                  (-> (map (seats_ids->seats params "booked")
                           (:seats_ids params))
                      (percistance-seat/update-seats-validated!)))]
      (when (and (some? booked-tickets)
                 (not (empty? booked-tickets)))
        (send email-sender (fn [sender]
                             (.send-email sender
                                   {:email
                                    (get-in params
                                            [:user :email])
                                    :message "Hi friend."}))))
      booked-tickets)))
