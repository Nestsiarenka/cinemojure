(ns cinema.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [cinema.layout :refer [error-page]]
            [cinema.routes.home :refer [home-routes]]
            [cinema.routes.auth :refer [auth-routes]]
            [compojure.route :as route]
            [cinema.env :refer [defaults]]
            [mount.core :as mount]
            [cinema.middleware :as middleware]
            [cinema.auth :as auth]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> (routes #'home-routes #'auth-routes)
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats)
        (wrap-routes auth/wrap-get-user-info)
        (wrap-routes auth/wrap-update-user-current-route))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
