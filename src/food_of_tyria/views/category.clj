(ns food-of-tyria.views.category
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- recipe-table [recipes]
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}
     [:tr
      [:th {:colspan 4 :style "border:1px solid #0aa;"} (-> recipes first :skill difficulty-to-tier)]]]
    (->> recipes
         (map item-cell)
         (partition-all 4)
         (map #(do [:tr %]))
         vec)))

(defn recipes-page [type]
  (apply page
     (->> (recipes/get-recipes)
          (filter #(= type (:type %)))
          (sort-by :skill)
          (partition-by #(difficulty-to-tier (% :skill)))
          (filter (complement empty?))
          (map recipe-table))))
