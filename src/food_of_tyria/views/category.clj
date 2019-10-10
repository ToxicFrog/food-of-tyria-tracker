(ns food-of-tyria.views.category
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- recipe-link [{id :id :as item}]
  [:td {:style "vertical-align:middle; text-align:left; width:25%"}
   [:img {:style "vertical-align:bottom;" :src (item :icon) :title (item :description)}]
   [:div {:style "display:inline-block"}
   [:a {:href (str "/items/" (item :id)) :id (str "label:" (item :id))}
    (item :name)]
   [:br]
   [:span {:id (str "button:" id)}
    [:input {:type "checkbox" :checked (item :cooked)
             :id (str id) :class "cooked-toggle"
             :onchange "toggleCooked(this);"}
     "🍴"]]
   [:br] "&nbsp;"
   ]])

(defn- recipe-table [recipes]
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}
     [:tr
      [:th {:colspan 4 :style "border:1px solid #0aa;"} (-> recipes first :skill difficulty-to-tier)]]]
    (->> recipes
         (map recipe-link)
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
