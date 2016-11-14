(ns cinema.routes.auth
  (:require [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                    context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]
            ))

(defn make-registration!
  [params]
  "Make registarion of user using cinema.auth"
  (let [registr-result (auth/registr! params)]
    (if (not (nil? (:id registr-result)))
      "make-session"
      (layout/render "login_registration.html"
                     (assoc registr-result
                            :form "registration")))))

(defroutes registration-routes
  (context "/registration" []
           (POST "/" {:keys [params]}
                 (make-registration! params))))


(defroutes login-routes
  (context "/login" []
           (GET "/" [] (layout/render "login_registration.html"
                                      (assoc {} :form "login")))
           (POST "/" [login pass]
                 (str "login: " login " pass: " pass))))

(def auth-routes
  (routes #'registration-routes #'login-routes))
