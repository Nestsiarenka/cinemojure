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
      (-> (response/found "/")
          (assoc :session {:id (:id registr-result)})
          )
      (layout/render "login_registration.html"
                     (assoc registr-result 
                            :form "registration")))))

(defroutes registration-routes
  (context "/registration" []
           (GET "/" [] (layout/render "login_registration.html"
                                      (assoc {} :form "registration")))
           (POST "/" {:keys [params session]}
                 (make-registration! params))))

(defroutes login-routes

  (context "/login" []
           (GET "/" [] (layout/render "login_registration.html"
                                      (assoc {} :form "login")))
           (POST "/" [login pass]
                 (str "login: " login " pass: " pass))))

(defroutes logout
  (auth/wrap-authenticated true "/hello"
                           (GET "/logout" []
                                (->
                                 (response/found "/login")
                                 (assoc :session nil)))))

(def auth-routes
  (auth/wrap-authenticated false "/"
                             login-routes
                             registration-routes
                             logout))
