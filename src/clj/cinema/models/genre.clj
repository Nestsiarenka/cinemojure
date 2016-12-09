(ns cinema.models.genre
  (:require [cinema.db.core :as db]
            [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]))

(defrecord Genre [id name]
  Validationable
  (validate-insert [this]
    (first (bouncer/validate this :name v/required)))
  (validate-update [this]
    "Unsupportable")
  (validate-delete [this]
    (first (bouncer/validate this :id [v/required v/number v/positive]))))
