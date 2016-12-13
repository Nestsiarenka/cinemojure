(ns cinema.routes.booking
  (:require [cinema.logic.booking :as booking]
            [cinema.logic.performancies :as performancies]
            [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]))

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

(defroutes booking
  (POST "/performancies/:session_id"
        [session_id :as {:keys [params]}]
        (book-seats! params)))

(def booking-routes (routes (auth/wrap-authenticated true "/" booking)))
