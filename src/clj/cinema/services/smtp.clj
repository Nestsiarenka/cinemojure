(ns cinema.services.smtp)

(def ^:dynamic *smtp-connection* {:address "smtp.txt"})

(defn reduce-data [before [key value]]
  (str before (str (name key) ": " value ";"))) 

(defn data->string [data]
  (reduce reduce-data "" data))

(defprotocol smtp-protocol
  (send-email [data]))

(defrecord Smtp []
    smtp-protocol
    (send-email [data] (spit (:address () *smtp-connection*))))
