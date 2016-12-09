(ns cinema.models.validators
  (:require [bouncer.validators :refer [defvalidator] :as v]))

(defvalidator sequence
  {:default-message-format "%s has to be sequential"}
  [value]
  (sequential? value))
