(ns cinema.percistance.genre
  (:require [cinema.db.core :as db]
            [cinema.models.genre :refer :all]))

(defn get-genres []
  (map map->Genre (db/get-genres)))

(defn add-genre! [genre]
  (let [errors (.validate-insert genre)]
    (if (nil? errors)
      (->> (db/add-genre! genre)
           (merge genre))
      {:errors errors :values genre})))

(defn delete-genre! [genre]
  (let [errors (.validate-delete genre)]
    (if (nil? errors)
      (db/delete-genre! genre)
      {:errors errors :values genre})))
