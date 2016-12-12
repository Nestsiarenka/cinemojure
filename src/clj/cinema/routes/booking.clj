(ns cinema.routes.booking
  (:require [cinema.logic.booking :as booking]
            [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    contet GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]))

(defn book-seats! [params]
  params)

(defroutes booking
  (context "/book" []
           (POST "/seats" {:keys [params]}
                 (book-seats!))))

(def booking-routes (routes (auth/wrap-authenticated true booking)))
