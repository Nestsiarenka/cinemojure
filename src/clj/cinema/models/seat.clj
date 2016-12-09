(ns cinema.models.seat
  (:require [cinema.db.core :as db]
            [cinema.models.protocols.validationable :refer :all]
            [bouncer.core :as bouncer]
            [bouncer.validators :as v]))

(defrecord Seat [session_id seat_id user_id seat_status seat_type seat_raw seat_number seat_cost])

