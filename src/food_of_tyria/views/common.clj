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

(defn page [& body]
  (html5
    [:head
     [:title "Food of Tyria"]
     (include-css "/css/screen.css")
     (include-js "/js/tyria.js")]
    [:body body]))

(def vcat (comp vec concat))
