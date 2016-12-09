(ns cinema.models.user
  (:require [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]))

(defrecord User [id firs_name last_name login email user_group last_login
                 is_active pass])
