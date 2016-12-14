(ns cinema.routes.home
  (:require [cinema.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page [params]
  (layout/render
   "home.html" (assoc params :docs (-> "docs/docs.md" io/resource slurp))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" {:keys [params]} (home-page params))
  (GET "/about" request (str request)))

