(ns food-of-tyria.views.ingredient
  (:require [food-of-tyria.views.common :refer :all]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- result-table [results]
  (println "result-table" results)
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}]
    (->> results
         (map item-cell)
         (partition-all 4)
         (map #(do [:tr %]))
         vec)))

(defn page-header [id item count]
  [:table {:align "center"}
   [:tr {:style "vertical-align:middle; text-align:center;"}
    [:td [:img {:style "vertical-align:middle;" :src (item :icon)}]]
    [:td {:style "vertical-align:middle; text-align:center;"}
     [:h1 {:style "margin:0px;" :id (str "label:" id)} (item :name)]
     (cond
       (= 0 count) "Not used in any recipes."
       (= 1 count) "Used in 1 recipe."
       :else (str "Used in " count " recipes."))
     " [API " [:a {:href (str "https://api.guildwars2.com/v2/items/" id)} "item"]
     "]"]
    [:td [:img {:style "vertical-align:middle;" :src (item :icon)}]]
    ]])

(defn ingredient-page [id]
  (let [item (recipes/get-item id)
        usage (recipes/get-usage id)]
    (println "ingredient-page" usage)
    (page
      (page-header id item (count usage))
      (if (seq usage)
       [:div {:align "center"}
        [:hr]
        (->> usage
             (sort-by :name)
             (result-table))]
       ""))))

