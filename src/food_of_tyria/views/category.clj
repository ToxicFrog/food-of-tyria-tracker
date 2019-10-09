(ns food-of-tyria.views.category
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(defn type-list []
  (->> (recipes/get-recipes)
       (map :type)
       (distinct)
       (sort)
       (mapcat (fn [type] [[:a {:href (str "/recipes/" type)} type] [:br]]))
       (apply page)))

(defn- recipe-link [item]
  [:td {:style "vertical-align:middle; text-align:left;"}
   [:img {:style "vertical-align:bottom;" :src (item :icon) :title (item :description)}]
   [:div {:style "display:inline-block"}
   [:a {:href (str "/items/" (item :id))} (item :name)]
   [:br]
   ; [:i "makes " (item :count 1)]
   ; [:br]
   [:input {:type "checkbox"} "🍴"] [:br] "&nbsp;"
   ]
   ])

(defn- recipe-table [recipes]
  (vcat
    [:table {:style "border:1px solid grey; width:100%;"}
     [:tr
      [:th {:colspan 4 :style "border:1px solid cyan;"} (-> recipes first :skill difficulty-to-tier)]]]
    (->> recipes
         (map recipe-link)
         (partition 4)
         (map #(do [:tr %]))
         vec)))

(defn recipes-page [type]
  (apply page
     (->> (recipes/get-recipes)
          (filter #(= type (:type %)))
          (sort-by :skill)
          (partition-by #(difficulty-to-tier (% :skill)))
          (map recipe-table))
     ))
