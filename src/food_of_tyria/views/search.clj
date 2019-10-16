(ns food-of-tyria.views.search
  (:require [food-of-tyria.views.common :refer :all]
            [clojure.string :as string]
            [food-of-tyria.models.recipes :as recipes]
            ))

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

(defn show-results [recipes]
  (->> recipes
       (sort-by #(get % :skill -1))
       (partition-by #(difficulty-to-tier (% :skill)))
       (filter (complement empty?))
       (map recipe-table)))

(defn search-results [query]
  (let [query (->> (string/split query #" ") (map string/lower-case) set)]
    (->> (recipes/search query)
         (show-results)
         (apply page))))

