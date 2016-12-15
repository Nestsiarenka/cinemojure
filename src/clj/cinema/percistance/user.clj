(ns cinema.percistance.user
  (:require
   [cinema.db.core :as db]
   [cinema.models.user :refer :all]
   [clojure.set :as set]))

(defn fill-user [user-info]
  (when (some? user-info)
        (-> user-info
            (assoc :user_group
                   (set/rename-keys
                    (select-keys
                     user-info [:user_groups_id :user_group])
                    {:user_groups_id :id :user_group :alias }))
            (dissoc :user_grocups_id)
            (map->User))))

(defn get-user-by-login [login]
  (fill-user (db/get-user-by-login {:login login})))

(defn get-user-oauth [oauth-id oauth-provider]
  (fill-user (db/get-user-oauth {:oauth_id oauth-id
                                 :oauth_provider oauth-provider})))

(defn create-user-force!
  [user]
  (->> 
   (assoc user :user_group_id (get-in user
                                      [:user_group :id]))
   (db/create-user!)
   (merge user)))

(defn create-user! [user]
  (let [errors (.validate-insert user)]
    (if (nil? errors)
      (create-user-force! user)
      {:errors errors :values user})))
