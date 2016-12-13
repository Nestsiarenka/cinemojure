(ns cinema.models.session
  (:require [cinema.db.core :as db]
            [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]))

(defrecord Session [id auditorium film begin_time]
  Validationable
  (validate-insert [this]
      (-> this
          (bouncer/validate [:auditorium :id] [v/required
                                                v/number
                                                v/positive] 
                            [:film :id] [v/required
                                         v/number
                                         v/positive])
          (first)))
  (validate-update [this]
    (merge (validate-insert this)
           (validate-delete this)))
  (validate-delete [this]
    (first (bouncer/validate
            this :id [v/required v/number v/positive]))))
