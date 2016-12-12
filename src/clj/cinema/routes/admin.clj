(ns cinema.routes.admin
  (:require [cinema.logic.admin :as admin]
            [cinema.layout :as layout]
            [compojure.core :refer [routes defroutes
                                   context GET POST]]
            [ring.util.http-response :as response]
            [cinema.auth :as auth]))

(defn congratulation-or-errors [add-result type params cong-message]
  (if (instance? type add-result)
    (assoc params :congratulation-message cong-message)
    (merge params add-result)))

(defn add-film! [params]
  (let [add-film-result (admin/add-film! params)]
    (layout/render
     "add-film.html"
     (-> add-film-result
         (congratulation-or-errors cinema.models.film.Film params
                                   "film added")
         (assoc :genres (admin/get-genres))))
    )
  )

(defn add-session! [params]
  (let [add-session-result (admin/add-session! params)]
    (layout/render
     "add-session.html"
     (-> add-session-result
         (congratulation-or-errors cinema.models.session.Session
                                   gparams
                                   "session added")
         (assoc :films (admin/get-films)
                :auditoriums (admin/get-auditoriums))
         ))))

(defroutes admin
  (context "/admin" []
           (GET "/add-film" {:keys [params]}
                (layout/render "add-film.html"
                               (assoc params
                                      :genres (admin/get-genres))))
           (POST "/add-film" {:keys [params]}
                 (add-film! params))
           (GET "/add-session" {:keys [params]}
                (layout/render "add-session.html"
                               (assoc params
                                      :films (admin/get-films)
                                      :auditoriums
                                      (admin/get-auditoriums))))
           (POST "/add-session" {:keys [params]}
                 (add-session! params))))

(def admin-routes
  (routes (auth/wrap-user-in-group ["admin"] "/" admin)))
