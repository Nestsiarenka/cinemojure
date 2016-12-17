(ns cinema.logic.booking
  (:require [cinema.percistance.seat :as percistance-seat]
            [cinema.models.seat :refer :all]
            [cinema.logic.util :as util]
            [clojure.edn :as edn]
            [cinema.services.smtp :refer :all]
            [cinema.auth :refer [loged-users update-in-loged-users]])
  (:import (java.util TimerTask Timer)))

(def email-sender (agent (->Smtp)))

(defn seats_ids->seats [params status]
  (fn [seat_id]
    (map->Seat {:seat_id (edn/read-string seat_id)
                :session_id (edn/read-string (:session_id params))
                :user_id (get-in params [:user :id])
                :seat_status status})))

(defn change-seats-status! [params status]
  (let [params (util/elem->list params :seats_ids)]
    (filter some?
            (-> (map (seats_ids->seats params status)
                     (:seats_ids params))
                (percistance-seat/update-seats-validated!)))))

(defn delete-from-cart [{:keys [user]}
                        [{ticket-id :id}]]
  (-> user
      (update-in [:cart :seats]
                 (fn [seats]
                   (filter #(not= (:id %) ticket-id) seats)))
      (assoc-in [:cart :change-time] (util/now))
      (update-in-loged-users)
      ))

(defn free-seats! [params]
  (let [free-tickets (change-seats-status! params "free")]
    (when free-tickets (and (some? free-tickets)
                            (not (empty? free-tickets)))
          (delete-from-cart params free-tickets))))

(defn add-to-cart [{:keys [user]}
                   incart-tickets]
  (-> user
   (update-in [:cart :seats] concat incart-tickets)
   (assoc-in [:cart :change-time] (util/now))
   (update-in-loged-users))
  )

(defn incart-seats! [params]
  (let [incart-tickets (change-seats-status! params "incart")]
    (when (and (some? incart-tickets)
               (not (empty? incart-tickets)))
      (add-to-cart params incart-tickets))))

(defn book-seats! [params]
  (let [booked-tickets (change-seats-status! params "booked")]
    (when (and (some? booked-tickets)
               (not (empty? booked-tickets)))
      (send email-sender (fn [sender]
                           (.send-email sender
                                        {:email
                                         (get-in params
                                                 [:user :email])
                                         :message "Hi friend."}))))
    booked-tickets))



(defn clear-cart [{{change-time :change-time seats :seats}
                   :cart :as user}]
  (let [current-time (util/now)]
    (if
        (>= 15
         (util/substract-dates-minutes change-time current-time))
      (do
        (->> seats
            (map #(assoc % :seat_status "free"))
            (percistance-seat/update-seats-validated!))
        (assoc user :cart nil))
      user)))

(defn check-carts []
  (swap! loged-users #(map clear-cart %)))

(let [task (proxy [TimerTask] []
             (run [] check-carts))]
  (. (new Timer) (schedule task (long 60000))))

