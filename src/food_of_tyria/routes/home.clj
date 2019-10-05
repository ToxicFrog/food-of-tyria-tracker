(ns food-of-tyria.routes.home
  (:require [compojure.core :refer :all]
            [compojure.coercions :refer [as-int]]
            [food-of-tyria.views.layout :as layout]
            ; [food-of-tyria.views :as views]
            [food-of-tyria.models.recipes :as recipes]))

(defn home []
  (layout/common [:h1 "Hello World!"]))

(defn item-tr [item]
  [:tr
   [:td [:img {:src (item :icon)
               :title (item :description)}]]
   [:td {:align "right"} [:b (item :count 1)]]
   [:td
    (if (item :ingredients)
      [:a {:href (str "/items/" (item :id))} (item :name)]
      (item :name))]])

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (layout/common
      [:h1
       [:img {:src (item :icon)}]
       (item :name)
       [:img {:src (item :icon)}]]
      [:div {:align "center" :width "50%"}
       [:h2 (item :type)]
       [:hr]
       (if (item :ingredients)
         (vec (concat [:table] (mapv item-tr (item :ingredients)))))]
      )))

(defn recipes-page []
  (->> (recipes/get-recipes)
       (mapv item-tr)
       (concat [:table])
       (vec)
       (layout/common)))

(defroutes home-routes
  (GET "/" [] (recipes-page))
  (GET "/items/:id" [id :<< as-int] (item-page id)))
