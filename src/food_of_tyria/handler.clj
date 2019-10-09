(ns food-of-tyria.handler
  (:require [compojure.core :refer [defroutes routes GET]]
            [compojure.coercions :refer [as-int]]
            ; [ring.middleware.resource :refer [wrap-resource]]
            ; [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [food-of-tyria.views :as views]
            [food-of-tyria.views.item :refer [item-page]]
            [food-of-tyria.models.recipes :as recipes]))

(defn init []
  (println "food-of-tyria is starting")
  (recipes/init))

(defn destroy []
  (println "food-of-tyria is shutting down")
  (recipes/deinit))

(defroutes app-routes
  (GET "/" [] (views/type-list))
  (GET "/recipes/:type" [type] (views/recipes-page type))
  (GET "/items/:id" [id :<< as-int] (item-page id))
  (GET "/items/:id/set-cooked" [id :<< as-int]
       (recipes/set-cooked! id true)
       "")
  (GET "/items/:id/unset-cooked" [id :<< as-int]
       (recipes/set-cooked! id false)
       "")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes app-routes)
      (handler/site)
      (wrap-base-url)))
