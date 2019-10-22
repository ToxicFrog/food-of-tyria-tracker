(ns food-of-tyria.views.ingredient
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- result-table [results]
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}]
    (->> results
         (map item-cell)
         (partition 4 4 (repeatedly (constantly [:td])))
         (map #(do [:tr %]))
         vec)))

(defn header [item count]
  (page-header item
    (cond
      (= 0 count) "Not used in any recipes."
      (= 1 count) "Used in 1 recipe."
      :else (str "Used in " count " recipes."))))

(defn ingredient-page [id]
  (let [item (recipes/get-item id)
        usage (recipes/get-usage id)]
    (page
      (header item (count usage))
      (if (seq usage)
       [:div {:align "center"}
        [:hr]
        (->> usage
             (sort-by :name)
             (result-table))]
       ""))))

