(ns cinema.logic.util)

(defn elem->list [map-value key]
  (if (not (sequential? (get map-value key)))
    (update map-value key list)
    map-value))
