(ns cinema.auth
  (:require [cinema.db.core :as db]
            [bouncer.core :refer [validate]]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            [compojure.core :refer [wrap-routes defroutes routes]]
            [ring.util.response :refer [redirect]]))

(def email-duplicated
  "ERROR: duplicate key value violates unique constraint \"users_email_key\"")
(def login-duplicated
  "ERROR: duplicate key value violates unique constraint \"users_login_key\"")

(defn handle-exception
  [e validated-registration-data]
  (let [exception-message (.getMessage e)]
    (if (.startsWith exception-message login-duplicated)
      (assoc-in validated-registration-data
                [:errors :login] '("login is already taken"))
      (if (.startsWith exception-message email-duplicated)
        (assoc-in validated-registration-data
                  [:errors :email] '("email is already taken"))
        (assoc validated-registration-data
               :another-error "something realy wrong happened"))))
  )

(defn validate-registration-data
  [registration-data]
  "Validates data provided by user 
with bouncer validation library"
  (zipmap [:errors :values]
          (validate registration-data
                    :login v/required
                    :email [v/required v/email]
                    :phone [[v/matches
                             #"^\+375((?:29)|(?:33)|(?:44))\d{7}$"]]
                    :pass [v/required [v/min-count 8]]
                    :repass [v/required
                             [v/matches ((fnil re-pattern "^$")
                                         (:pass registration-data))]]
                    :first-name [v/string v/required]
                    :second-name [v/string v/required])))


(defn add-user-to-db!
  [validated-registration-data]
  "Adding user to database"  (if (nil? (:errors validated-registration-data))
    (try 
      (->>
       (get-in validated-registration-data [:values :pass])
       (hashers/derive)
       (assoc (:values validated-registration-data) :user-group-id 2 :pass)       
       (db/create-user!))      
      (catch Exception e
        (handle-exception e
                          validated-registration-data)))
    validated-registration-data))

(def registr!
  (comp add-user-to-db!
        validate-registration-data))


(defn is-user?
  [{:keys [predicate id]}]
  (predicate (db/get-user {:id id})))

(defn wrap-is-user-params
  [predicate id]
  {:predicate predicate :id id})

(defn is-user-active-predicate []
  (fn [user] (and (some? user) (:is_active user))))

(defn is-user-in-group-predicate
  [group-alias]
  (fn [user] (and ((is-user-active-predicate) user)
                  (= (:alias (db/get-user-role user)) group-alias))))

(defn launch-is-user-check
  [predicate id predicate-params]
  (-> (apply predicate predicate-params)
      (wrap-is-user-params id)
      (is-user?)))

(defn is-user-active?
  [id]
  (launch-is-user-check is-user-active-predicate id []))

(defn is-user-in-group?
  [id group-alias]
  (launch-is-user-check is-user-in-group-predicate id [group-alias]))


(defn wrap-authenticated
  [value routes-vector redirect-path]
  (let [middleware
        (fn [handler]
          (fn [{:keys [session] :as request}]
            (if (= value (is-user-active? session))        
              (handler request)
              (redirect redirect-path))))]
    (->>
     (map (fn [route]
            (wrap-routes route middleware))
          routes-vector)
     (apply routes ))))

