(ns cinema.percistance.session
  (:require [cinema.db.core :as db]
            [cinema.models.film :refer :all]
            [cinema.models.auditorium :refer :all]
            [cinema.models.session :refer :all]
            [cinema.models.seat :refer :all]))


(defn fill-session [mapping-param]
  (->  mapping-param
          (dissoc :auditorium_id :film_id)
          map->Session  
          (assoc :film (map->Film
                        (assoc (db/get-film-by-id
                                {:id (:film_id  mapping-param)})
                               :genres (db/get-films-genres mapping-param)))
                 :auditorium (db/get-auditorium-by-id
                              mapping-param)
                 :seats (map map->Seat (db/get-sessions-seats
                                       {:session_id (:id mapping-param)})))))

(defn get-sessions []
  (map fill-session
       (db/get-sessions)))

(defn get-session-by-id [session_id]
  (fill-session (db/get-session-by-id {:id session_id})))


(defn add-session! [session]
  (let [errors (.validate-insert session)]
    (if (nil? errors)
      (merge session (-> session
                        (assoc :auditorium_id (get-in
                                               session
                                               [:auditorium :id])
                               :film_id (get-in session [:film :id]))       
                        (db/add-session!)
                        ))
      {:errors errors :values session})))

(defn get-sessions-by-film
  [film]
  (->>
   {:film_id (:id film)}
   (db/get-sessions-by-film)
   (map fill-session)))

(defn delete-session! [session]
  (let [errors (.validate-delete session)]
    (if (nil? errors)
      (db/delete-session! session)
      {:errors errors :values session})))
