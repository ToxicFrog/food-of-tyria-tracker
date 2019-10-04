(ns food-of-tyria.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [food-of-tyria.routes.home :refer [home-routes]]
            [food-of-tyria.models.recipes :as recipes]))

(defn init []
  (println "food-of-tyria is starting")
  (recipes/init))

(defn destroy []
  (println "food-of-tyria is shutting down")
  (recipes/deinit))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
