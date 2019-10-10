(ns food-of-tyria.views.common
  (:require
    [hiccup.page :refer [html5 include-css include-js]]
    [food-of-tyria.models.recipes :as recipes]))

(defn difficulty-to-tier [rating]
  (condp > rating
     75 "Novice"
    150 "Initiate"
    225 "Apprentice"
    300 "Journeyman"
    400 "Adept"
    500 "Master"
    999 "Grandmaster"))

(defn type-list []
  (->> (recipes/get-recipes)
       (map :type)
       (distinct)
       (sort)
       (map (fn [type]
              [[:a {:href (str "/recipes/" type)} type]
               " (" (recipes/cooked-percentage type) ") "]))
       (interpose [[:b " | "]])
       (mapcat identity)
       (concat [:div {:style "width:100%; text-align:center;"}])
       (vec)))

(defn page [& body]
  (html5
    [:head
     [:title "Food of Tyria"]
     (include-css "/css/screen.css")
     (include-js "/js/tyria.js")]
    [:body {:onload "updateAllStyles();"}
      (type-list)
      [:hr]
      body]))

(def vcat (comp vec concat))
