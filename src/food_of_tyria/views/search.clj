(ns food-of-tyria.views.search
  (:require [food-of-tyria.views.common :refer :all]
            [clojure.string :as string]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- ingredient-link [{id :id :as item}]
  [:td {:style "vertical-align:middle; text-align:left; width:25%"}
   [:img {:style "vertical-align:middle;" :src (item :icon) :title (item :description)}]
   (item :name)])

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

(defn- result-table [results]
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}]
    (->> results
         (map #(if (% :ingredients) (recipe-link %) (ingredient-link %)))
         (partition-all 4)
         (map #(do [:tr %]))
         vec)))

(defn search-results [query]
  (let [query (->> (string/split query #" ") (map string/lower-case) set)]
    (->> (recipes/search query)
         (sort-by :name)
         (result-table)
         (page))))
