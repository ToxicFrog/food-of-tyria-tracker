(ns food-of-tyria.routes.home
  (:require [compojure.core :refer :all]
            [food-of-tyria.views.layout :as layout]
            [food-of-tyria.views :as views]
            [food-of-tyria.models.recipes :as recipes]))

(defn home []
  (layout/common [:h1 "Hello World!"]))

(defn item-to-html [item]
  [:tr
   [:td [:img {:src (item :icon)
               :title (item :description)}]]
   [:td {:align "right"} [:b (item :count 1)]]
   [:td (item :name)]])

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (println item)
    (layout/common
      [:h1
       [:img {:src (item :icon)}]
       (item :name)
       [:img {:src (item :icon)}]]
      [:div {:align "center" :width "50%"}
       [:h2 (item :type)]
       [:hr]
       (vec
         (concat [:table]
                 (->> (-> item :recipe :ingredients) (mapv item-to-html))))]
      )))

(defroutes home-routes
  (GET "/" [] (views/recipes))
  (GET "/items/:id" [id] (views/item id)))
