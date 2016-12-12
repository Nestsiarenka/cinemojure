(ns cinema.percistance.film
  (:require [cinema.db.core :refer [*db*] :as db]
            [cinema.models.genre :refer :all]
            [cinema.models.session :refer :all]
            [cinema.models.film :refer :all]
            [conman.core :as conman]))

(defn get-films []
  (map (fn [mapping-param]
         (-> mapping-param
             map->Film
             (assoc :genres
                    (map map->Genre (db/get-films-genres
                             {:film_id (:id mapping-param)})))
             ))
      (db/get-films)))

(defn get-film-by-id [id]
  (-> id
      (assoc {} :id)
      (map->Film (db/get-film-by-id))))

(defn add-film! [film]
  (let [errors (.validate-insert film)]
    (if (nil? errors)
      (do (conman/with-transaction [*db*]
            (let [added-film (->> film
                                  (db/add-film!)
                                  (merge film))]
              (->> (:genres film)
                   (map #(conj [(:id added-film)]
                               (:id %)))                   
                   (hash-map :films_genres)
                   (db/add-films-genres!))
              (assoc added-film :genres
                     (db/get-films-genres
                      {:film_id (:id added-film)})))) )

      {:errors errors :values film})))

(defn delete-film! [film]
  (let [errors (.validate-delete film)]
    (if (nil? errors)
      (db/delete-film! film)
      {:errors errors :values film})))

