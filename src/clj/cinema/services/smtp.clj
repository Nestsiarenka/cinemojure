(ns cinema.services.smtp)

(def ^:dynamic *smtp-connection* {:address "D:/smtp.txt"})

(defn reduce-data [before [key value]]
  (str before (str (name key) ": " value ";"))) 

(defn data->string [data]
  (reduce reduce-data "" data))

(defprotocol smtp-protocol
  (send-email [x data]))

(defrecord Smtp []
    smtp-protocol
    (send-email [x data]
      (spit (:address *smtp-connection*)
            (data->string data) :append true)))
