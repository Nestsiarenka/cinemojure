(ns cinema.logic.admin
  (:require
   [cinema.models.film :refer :all]
   [cinema.models.session :refer :all]
   [cinema.percistance.film :as percistance-film]
   [cinema.percistance.session :as percistance-session]
   [cinema.percistance.auditorium :as percistance-auditorium]
   [cinema.percistance.genre :as percistance-genre]
   [clojure.edn :as edn]
   [clojure.set :as set]))

(def get-genres percistance-genre/get-genres)

(def get-films percistance-film/get-films)

(def get-auditoriums percistance-auditorium/get-auditoriums )


(defn add-film! [params]
  (let [params (when (not (seq? (:genres params)))
                 (assoc params :genres (list (:genres params))))]
    (-> (select-keys params [:title :duration_minutes
                             :age_limit :logo_url :genres])
        (update :genres (fn [vector]
                          (map #(hash-map :id (edn/read-string %)) vector)))
        (update :age_limit edn/read-string)
        (update :duration_minutes edn/read-string)
        (map->Film)
        (percistance-film/add-film!))))

(defn make-map-id [value]
  (hash-map :id (edn/read-string value)))

(defn string->date [string format]
  (.format (java.text.SimpleDateFormat. format) string))

(defn add-session! [params]
  (->
   params
   (select-keys [:film_id :auditorium_id :begin_time])
   (update :film_id make-map-id)
   (update :auditorium_id make-map-id)
   (set/rename-keys {:film_id :film :auditorium_id :auditorium})
   (map->Session)
   (percistance-session/add-session!)))
