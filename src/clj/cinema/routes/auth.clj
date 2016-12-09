(ns cinema.routes.auth
  (:require [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]
            ))

(defn write-session
  [id-or-error-map previous-operation]
  (if (not (nil? (:id id-or-error-map)))
    (-> (response/found "/")
        (assoc :session {:id (:id id-or-error-map)}))
    (layout/render "login_registration.html"
                   (assoc id-or-error-map :form
                          previous-operation))))

(defn make-registration!
  [params]
  "Make registarion of user using cinema.auth"
  (-> (auth/registr! params)
      (write-session "registration")))

(defroutes registration-routes
  (context "/registration" []
           (GET "/" [] (layout/render "login_registration.html"
                                      {:form "registration"}))
           (POST "/" {:keys [params session]}
                 (make-registration! params))))

(defn login
  [params]
  (-> (auth/login params)
      (write-session "login")))

(defroutes login-routes

  (context "/login" []
           (GET "/" [] (layout/render "login_registration.html"
                                       {:form "login"}))
           (POST "/" {:keys [params]}
                 (login params))))

(defroutes logout
  (auth/wrap-authenticated true "/login"
                           (GET "/logout" [session]
                                (->
                                 (response/found "/login")
                                 (assoc :session nil)))))

(def auth-routes
  (routes 
   (auth/wrap-authenticated false "/"
                            login-routes
                            registration-routes)
   logout))
