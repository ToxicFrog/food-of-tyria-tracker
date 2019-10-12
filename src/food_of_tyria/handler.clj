(ns food-of-tyria.handler
  (:require [compojure.core :refer [defroutes routes GET]]
            [compojure.coercions :refer [as-int]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [redirect]]
            [food-of-tyria.views.ingredient :refer [ingredient-page]]
            [food-of-tyria.views.search :refer [search-results]]
            [food-of-tyria.views.item :refer [item-page]]
            [food-of-tyria.views.category :refer [recipes-page]]
            [food-of-tyria.models.recipes :as recipes]))

(defn init []
  (println "food-of-tyria is starting")
  (recipes/init))

(defn destroy []
  (println "food-of-tyria is shutting down")
  (recipes/deinit))

(defroutes app-routes
  (GET "/" [] (redirect "/recipes/IngredientCooking"))
  (GET "/recipes/:type" [type] (recipes-page type))
  (GET "/items/:id" [id :<< as-int] (item-page id))
  (GET "/items/:id/set-cooked" [id :<< as-int]
       (recipes/set-cooked! id true)
       "")
  (GET "/items/:id/unset-cooked" [id :<< as-int]
       (recipes/set-cooked! id false)
       "")
  (GET "/ingredient/:id" [id :<< as-int] (ingredient-page id))
  (GET "/search" [q] (search-results q))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes app-routes)
      (handler/site)
      (wrap-base-url)))
