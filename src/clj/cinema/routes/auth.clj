(ns cinema.routes.auth
  (:require [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]
            ))

(defn redirect-home-with-session [user]
  (-> (response/found "/")
        (assoc :session {:id (:id user)
                         :login-time (:login-time
                                      user)})))

(defn render-login-registration
  [user-or-error-map previous-operation]
  (if (not (nil? (:id user-or-error-map)))
    (redirect-home-with-session user-or-error-map)
    (layout/render "login_registration.html"
                   (assoc user-or-error-map :form
                          previous-operation))))

(defn make-registration!
  [params]
  "Make registarion of user using cinema.auth"
  (-> (auth/registr! params)
      (render-login-registration "registration")))

(defroutes registration-routes
  (context "/registration" []
           (GET "/" [] (layout/render "login_registration.html"
                                      {:form "registration"}))
           (POST "/" {:keys [params]}
                 (make-registration! params))))

(defn login
  [params]
  (-> (auth/login params)
      (render-login-registration "login")))

(defroutes login-routes

  (context "/login" []
           (GET "/" [] (layout/render "login_registration.html"
                                      {:form "login"}))
           (POST "/" {:keys [params]}
                 (login params)))
  
(GET "/login/google" request
     (let [user-or-errors
              (auth/do-authorized-google
               request
               #(auth/find-or-create-user-google! %))]
       (if (instance? cinema.models.user.User user-or-errors)
         (redirect-home-with-session user-or-errors)
          user-or-errors)))

  (GET "/oauth2-callback-google" request
       (auth/oauth2-call-back-handler-google request)))

(defroutes logout
  (auth/wrap-authenticated true "/login"
                           (GET "/logout" {:keys [session]}
                                (auth/logout session)
                                (->
                                 (response/found "/login")
                                 (assoc :session nil)))))

(def auth-routes
  (routes 
   (auth/wrap-authenticated false "/"
                            login-routes
                            registration-routes)
   logout))
