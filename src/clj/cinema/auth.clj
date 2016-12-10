(ns cinema.auth
  (:require [cinema.db.core :as db]
            [bouncer.core :refer [validate]]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            [compojure.core :refer [wrap-routes defroutes routes]]
            [ring.util.response :refer [redirect]]
            [cinema.percistance.user :refer :all]
            [cinema.models.user :refer :all]))

(def loged-users (atom ()))

(def email-duplicated
  "ERROR: duplicate key value violates unique constraint \"users_email_key\"")
(def login-duplicated
  "ERROR: duplicate key value violates unique constraint \"users_login_key\"")
(def login-password-wrong
  "such login and password combination is incorrect")

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

(defn select-values [map ks]
         (reduce #(conj %1 (map %2)) [] ks))

(defn prepare-data-for-user-creation [values]
  (-> values
      (assoc :user-group {:id 2}
             :pass (hashers/derive (:pass values)))
      (select-values
       [:id :first-name :second-name :login :email
        :user-group :last-login :is-active :pass])))

(defn add-user-to-db!
  [validated-registration-data]
  "Adding user to database"
  (if (nil? (:errors validated-registration-data))
    (try
      (let [values (:values validated-registration-data)]
        (let [init-data (prepare-data-for-user-creation values)]
          (create-user! (apply ->User init-data)))) 
      (catch Exception e
        (handle-exception e
                         validated-registration-data)))
    validated-registration-data))

(defn now []
  (str (.getTime (new java.util.Date))))

(defn add-to-loged-users [user-or-errors]
  (if (instance? cinema.models.user.User user-or-errors)
    (do
      (let [user (assoc user-or-errors :login-time (now))]
        (swap! loged-users conj user)
        user))
    (user-or-errors))
  )

(def registr!
  (comp
   add-to-loged-users
   add-user-to-db!
   validate-registration-data))

(defn login
  [{:keys [pass login] :as login-data}]
  (let [user (get-user-by-login login)]
    (if (hashers/check pass (:pass user))
      (add-to-loged-users user)
      {:values login-data :another-error login-password-wrong}))
  )

(defn user-find-predicate [user-id login-time]
  (fn [user] (and (= user-id (:id user))
                  (= login-time (:login-time user)))))

(defn delete-from-loged-users
  [{:keys [id login-time]}]
  (swap! loged-users (fn [coll]
                       (remove
                        (user-find-predicate id
                                             login-time)
                        coll))))

(def logout delete-from-loged-users)

(defn get-from-loged-users
  [user-id login-time]
  (->
   #(when ((user-find-predicate user-id login-time) %) %)
   (some @loged-users)
   )) 

(defn is-user?
  [{:keys [predicate id login-time]}]
  (predicate (get-from-loged-users id login-time)))

(defn wrap-is-user-params
  [predicate id login-time]
  {:predicate predicate
   :id id
   :login-time login-time})

(defn is-user-active-predicate []
  (fn [user] (and (some? user) (:is_active user))))

(defn is-user-in-group-predicate
  [group-alias]
  (fn [user] (and ((is-user-active-predicate) user)
                  (= (get-in user [:user_group :alias])
                     group-alias))))

(defn launch-is-user-check
  [predicate id login-time predicate-params]
  (-> (apply predicate predicate-params)
      (wrap-is-user-params id login-time)
      (is-user?)))

(defn is-user-active?
  [{:keys [id login-time]}]
  (launch-is-user-check is-user-active-predicate
                        id login-time []))

(defn is-user-in-group?
  [{:keys [id login-time]} group-alias]
  (launch-is-user-check is-user-in-group-predicate id
                        login-time [group-alias]))

(defn get-wrap-authenticated-middleware
  [value redirect-path]
  (fn [handler]
    (fn [{:keys [session] :as request}]
      (if (= value (is-user-active? session))        
        (handler request)
        (redirect redirect-path))))
        )

(defn map-wrap-routes
  [middleware routes-vector]
  (->>
       (map (fn [route]
              (wrap-routes route middleware))
            routes-vector)
       (apply routes ))
  )

(defn wrap-authenticated
  [allow redirect-path & routes-vector]
  (-> (get-wrap-authenticated-middleware
       allow redirect-path)
      (map-wrap-routes routes-vector)))

(defn get-wrap-user-in-group-middleware
  [groups redirect-path]
  (fn [handler]
    (fn [{:keys [session] :as request}]
      (if (some (partial is-user-in-group? session) groups)
        (handler request)
        (redirect redirect-path)))))

(defn wrap-user-in-group
  [groups redirect-path & routes-vector]
 (-> (get-wrap-user-in-group-middleware groups redirect-path)
      (map-wrap-routes routes-vector)))

(defn wrap-get-user-info
  [handler]
  (fn [{:keys [session] :as request}]
    (handler (if (contains? session :id)
               (->> (get-from-loged-users (:id session)
                                          (:login-time session))
                    (assoc-in request [:params :user]))
               request))
    ))

(defn updating-algorithm
  [{:keys [id login-time] :as new-user-info}]
  (fn [old-user-info]
    (if ((user-find-predicate id login-time) old-user-info)
      new-user-info
      old-user-info
      )))

(defn update-in-loged-users
  [{:keys [id login-time] :as user}]
  (swap! loged-users #(map (updating-algorithm user) %)))

(defn wrap-update-user-current-route
  [handler]
  (fn [{:keys [session] :as request}]
    (let [user (get-from-loged-users (:id session)
                                     (:login-time session))]
      (-> user
          (assoc :current-route (get-in request
                                        [:compojure/route
                                         1]))
          (update-in-loged-users)))
    (handler request)
    ))

(defn users-in-route
  [route-string]
  (-> (filter
       #(= route-string
           (:current-route %))
       @loged-users)
      (count)))
