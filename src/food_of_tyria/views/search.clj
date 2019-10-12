(ns food-of-tyria.views.search
  (:require [food-of-tyria.views.common :refer :all]
            [clojure.string :as string]
            [food-of-tyria.models.recipes :as recipes] ))

(defn- result-table [results]
  (vcat
    [:table {:style "border:0px solid grey; width:100%;"}]
    (->> results
         (map item-cell)
         (partition-all 4)
         (map #(do [:tr %]))
         vec)))

(defn search-results [query]
  (let [query (->> (string/split query #" ") (map string/lower-case) set)]
    (->> (recipes/search query)
         (sort-by :name)
         (result-table)
         (page))))
