(ns food-of-tyria.views.category
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.views.search :as search]
            [food-of-tyria.models.recipes :as recipes]))

(defn recipes-page [type]
  (apply page
     (->> (recipes/get-recipes)
          (filter #(= type (:type %)))
          (search/show-results))))
