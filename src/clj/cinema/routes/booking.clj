(ns cinema.routes.booking
  (:require [cinema.logic.booking :as booking]
            [cinema.logic.performancies :as performancies]
            [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST DELETE]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]))

(defn incart-seats! [{:keys [session_id] :as params}]
  (let [incart-tickets (booking/incart-seats! params)]
    (layout/render
     "performance.html"
     (->
      (if (or (nil? incart-tickets) (empty? incart-tickets))
        (assoc params :another-error
               "Tickets were not in cart. Call admin, please.")
        (do             
          (assoc params :bought-tickets incart-tickets)))
         (assoc :session (performancies/get-session-by-id session_id)
                :users-in-route (auth/users-in-route
                                  (str "/performancies/" session_id)))))))

(defn book-seats! [{:keys [session_id] :as params}]
  (let [bought-tickets (booking/book-seats! params)]
    (layout/render
     "performance.html"
     (->
      (if (or (nil? bought-tickets) (empty? bought-tickets))
        (assoc params :another-error
               "Tickets were not bought. Call admin, please.")
        (do             
          (assoc params :bought-tickets bought-tickets)))
         (assoc :session (performancies/get-session-by-id session_id)
                :users-in-route (auth/users-in-route
                                  (str "/performancies/" session_id)))))))

(defn get-cart
  [{user :user}]
  (layout/render "cart.html"
   {:user user}))

(defroutes booking
  (POST "/performancies/:session_id"
        [session_id :as {:keys [params]}]
        (incart-seats! params))
  (GET "/cart" {:keys [params]}
       (get-cart params))
  (DELETE "/cart" {:keys [params]}
          (booking/free-seats! params))
  (POST "/cart"
        {:keys [params]}
        (book-seats! params)))

(def booking-routes (routes (auth/wrap-authenticated true "/" booking)))
