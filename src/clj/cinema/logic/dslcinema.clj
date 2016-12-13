(ns cinema.logic.dslcinema
  (:require
   [cinema.models.film :refer :all]
   [cinema.models.session :refer :all]
   [cinema.percistance.film :as percistance-film]
   [cinema.percistance.session :as percistance-session]
   [cinema.percistance.auditorium :as percistance-auditorium]
   [cinema.percistance.genre :as percistance-genre]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [cinema.logic.util :as util]))

(defn string->int [value]
  (edn/read-string value))

(defn vector->ids-vector [vector-or-single]
  (let [exactly-vector
        (if-not (sequential? vector-or-single)
          (list vector-or-single)
          vector-or-single)]
    (map #(hash-map :id (string->int %)) exactly-vector)))

(defn string->date [format string]
  (.parse (java.text.SimpleDateFormat. format) string))

(def conversions
  {:film [:title identity
          :duration_minutes string->int
          :age_limit string->int
          :logo_url identity
          :genres vector->ids-vector]
   :sessions [:auditorium vector->ids-vector
              :film vector->ids-vector
              :begin_time (partial string->date "yyyy-MM-dd'T'HH:mm")]})

(defn convert [type data]
  (reduce (fn [before [to-convert-keyword convertion]]
            (let [to-convert-data (get data to-convert-keyword)]
              (when (and (some? before) (some? to-convert-data))
                    (assoc before
                           to-convert-keyword
                           (convertion to-convert-data))))) {}
          (partition 2 (get conversions type))))

(defmacro if-converted [type data converted-data-name & then-else]
  `(let [~converted-data-name (convert ~type ~data)]
     (if-not (nil? ~converted-data-name)
       ~@then-else)))

(defmulti add* (fn [type data & another] type))

(defmethod add* :film
  [type data & another]
  (if-converted type data converted-data
                (percistance-film/add-film!
                 (map->Film converted-data))
     {:errors "Passed type doesn't exist" :values data}))

(defmethod add* :session
  [type data & another]
  (if-converted type data converted-data
                (percistance-session/add-session!
                 (map->Session converted-data))
                {:errors "Passed type doesn't exist" :values data}))

(defmacro add [type data]
  `(add* ~(keyword type) ~data))
