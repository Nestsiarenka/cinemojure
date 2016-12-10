(ns cinema.percistance.user
  (:require
   [cinema.db.core :as db]
   [cinema.models.user :refer :all]
   [clojure.set :as set]))

(defn get-user-by-login [login]
  (let [user-info (db/get-user-by-login {:login login})]
    (-> user-info
        (assoc :user_group
               (set/rename-keys
                (select-keys
                 user-info [:user_groups_id :user_group])
                {:user_groups_id :id :user_group :alias }))
        (dissoc :user_groups_id)
        (map->User))))

(defn create-user! [user]
  (let [errors (.validate-insert user)]
    (if (nil? errors)
      (do
        (->> 
            (assoc user :user_group_id (get-in user
                                          [:user_group :id]))
            (db/create-user!)
            (merge user)))
      {:errors errors :values user})))
