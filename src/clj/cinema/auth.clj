(ns cinema.auth
  (:require [cinema.db.core :as db]
            [bouncer.core :refer [validate]]
            [bouncer.validators :as v]))

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
                             [v/matches ((fnil re-pattern "")
                                         (:pass registration-data))]]
                    :first-name [v/string v/required]
                    :second-name [v/string v/required])))

(defn add-user-to-db!
  [validated-registration-data]
  "Adding user to database"
  (if (nil? (:errors validated-registration-data))
    "nothing-to-say"
    validated-registration-data))

(def registr!
  (comp add-user-to-db!
        validate-registration-data))
