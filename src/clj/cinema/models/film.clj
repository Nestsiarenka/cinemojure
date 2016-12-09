(ns cinema.models.film
  (:require [cinema.models.genre :refer :all]
            [cinema.models.session :refer :all]
            [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]
            [cinema.models.validators :as mv]))

(def genres-required-id "every genre has to have possitive id")

(defrecord Film [id title duration_minutes age_limit logo_url
                 genres]
  Validationable
  (validate-insert [this]
    (-> this
        (bouncer/validate :title v/required
                          :duration_minutes [v/required
                                             v/number
                                             v/positive]
                          :age_limit [v/required
                                      v/number
                                      v/positive]
                          :genres [mv/sequence [v/every
                                    #(bouncer/valid?
                                      % :id [v/required
                                             v/number
                                             v/positive])
                                    :message
                                    genres-required-id]]
                          :logo_url [v/required])
        (first)))
  (validate-update [this]
    (merge (validate-insert this)
           (validate-delete this)))
  (validate-delete [this]
     (first (bouncer/validate
             this :id [v/required v/number v/positive]))))



