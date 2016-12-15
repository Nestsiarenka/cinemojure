(ns cinema.models.user
  (:require [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]))

(defrecord User [id first_name second_name login email user_group
                 last_login is_active pass oauth_id oauth_provider]
  Validationable
  (validate-insert [this]
    (first (bouncer/validate this
                             :login [v/required]
                             :email [v/required]
                             :pass  [v/required]
                             :first_name [v/required]
                             :second_name [v/required]))))
