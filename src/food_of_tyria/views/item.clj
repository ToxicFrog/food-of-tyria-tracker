(ns food-of-tyria.views.item
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(declare ingredient-table)

(defn- item-tr [item]
  [[:tr
    (item-cell item :colspan 2 :class "recipe-details")
   (if (item :ingredients)
     [:tr
      [:td {:style "font-weight:bold; font-size:60px; vertical-align:top;"} "↳"]
      [:td (ingredient-table (item :ingredients) {:width "100%"})]]
     "")]])

(defn- ingredient-table [ingredients attrs]
  (vcat [:table (merge {:style "border:1px solid grey;"} attrs)]
        (mapcat item-tr ingredients)))

(defn cooked-toggle [{id :id :as item}]
  [:span {:id (str "button:" id)}
   [:input {:type "checkbox" :checked (item :cooked)
            :id (str id) :class "cooked-toggle"
            :onchange "toggleCooked(this);"}
    "🍴"]])

(defn header [item]
  (page-header item
    [:span
     [:i
      (difficulty-to-tier (item :skill)) " " (item :type) " -- makes " (item :count)]
     "&nbsp;"
     (cooked-toggle item)]))

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (page
      (header item)
      [:div {:align "center"}
       [:hr]
       ; TODO lay out ingredient tables side by side to reduce scrolling
       (if (item :ingredients)
         (ingredient-table (item :ingredients) {}))])))
