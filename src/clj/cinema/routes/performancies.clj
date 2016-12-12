(ns cinema.routes.performancies
  (:require [cinema.logic.performancies :refer :all]
            [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]))


(defroutes performancies
  (GET "/performancies" {:keys [params]}
       (layout/render "performancies.html"
                      (assoc params :films (get-films))))
  (auth/wrap-authenticated
   true "/login"
   (GET "/performancies/:id" [id :as {:keys [params]}]
        (layout/render "performance.html"
                       (assoc params
                              :session (get-session-by-id id)
                              :users-in-route
                              (auth/users-in-route
                               (str "/performancies/" id)))))))

(def performancies-routes
  (routes #'performancies))
