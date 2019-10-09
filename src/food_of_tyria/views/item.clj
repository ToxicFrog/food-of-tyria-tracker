(ns food-of-tyria.views.item
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(declare ingredient-table)

(defn- item-tr [item]
  [[:tr
    [:td {:style "vertical-align:top;"}
     [:img {:src (item :icon)
            :title (item :description)}]]
    [:td {:align "right"} [:b (item :count 1)]]
    (if (item :ingredients)
      [:td [:a {:href (str "/items/" (item :id))} (item :name)]]
      [:td (item :name)])]
   (if (item :ingredients)
     [:tr
      [:td {:colspan 2 :style "font-weight:bold; font-size:60px; vertical-align:top;"} "↳"]
      [:td (ingredient-table (item :ingredients) {:width "100%"})]]
     "")])

(defn- ingredient-table [ingredients attrs]
  (vcat [:table (merge {:style "border:1px solid grey;"} attrs)]
        (mapcat item-tr ingredients)))

(defn type-list []
  (->> (recipes/get-recipes)
       (map :type)
       (distinct)
       (sort)
       (mapcat (fn [type] [[:a {:href (str "/recipes/" type)} type] [:br]]))
       (apply page)))

(defn cooked-toggle [id item]
  [:span {:id (str "button:" id)}
   [:input {:type "checkbox" :checked (item :cooked)
            :id (str id) :class "cooked-toggle"
            :onchange "toggleCooked(this);"}
    "🍴"]])

(defn page-header [id item]
  [:table {:align "center"}
   [:tr {:style "vertical-align:middle; text-align:center;"}
    [:td [:img {:style "vertical-align:middle;" :src (item :icon)}]]
    [:td {:style "vertical-align:middle; text-align:center;"}
     [:h1 {:style "margin:0px;" :id (str "label:" id)} (item :name)]
     [:i [:a {:href (str "/recipes/" (item :type))} (item :type)] " -- makes " (item :count)]
     " [" [:a {:href (str "https://api.guildwars2.com/v2/items/" id)} "item"]
     " " [:a {:href (str "https://api.guildwars2.com/v2/recipes/" (item :recipe-id))} "recipe"]
     "]"
     (cooked-toggle id item)
    [:td [:img {:style "vertical-align:middle;" :src (item :icon)}]]]
    ]])

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (page
      (page-header id item)
      [:div {:align "center"}
       [:hr]
       ; TODO lay out ingredient tables side by side to reduce scrolling
       (if (item :ingredients)
         (ingredient-table (item :ingredients) {}))])))
