(ns food-of-tyria.views.search
  (:require [food-of-tyria.views.common :refer :all]
            [clojure.string :as string]
            [food-of-tyria.models.recipes :as recipes]
            ))

(defn- recipe-table [recipes]
  (let [cooked (count (filter :cooked recipes))
        total (count recipes)
        percent (quot (* 100 cooked) total)
        cls (condp >= (quot (* 100 cooked) total)
                0 "not-started"
               33 "low-progress"
               66 "medium-progress"
               99 "high-progress"
              100 "completed")]
    (vcat
      [:table.search-result
       [:tr.header
        [:th {:colspan 2}
         (-> recipes first :skill difficulty-to-tier)]
        [:th {:colspan 2}
         [:progress {:min 0 :value cooked :max total :class cls}]
         "&nbsp;(" percent "%)"]]]
      (->> recipes
           (map item-cell)
           (partition 4 4 (repeatedly (constantly [:td])))
           (map #(do [:tr %]))
           vec))))

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
