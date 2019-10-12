(ns food-of-tyria.views.common
  (:require
    [hiccup.page :refer [html5 include-css include-js]]
    [food-of-tyria.models.recipes :as recipes]))

(defn- ingredient-cell [{id :id :as item}]
  [:td {:style "vertical-align:middle; text-align:left; width:25%"}
   [:img {:style "vertical-align:middle;" :src (item :icon) :title (item :description)}]
   (item :name)])

(defn- recipe-cell [{id :id :as item}]
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

(defn item-cell [item]
  (if (item :ingredients)
    (recipe-cell item)
    (ingredient-cell item)))

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
      (vec (concat
        (type-list)
        [[:form {:action "/search" :method "get" :style "display:inline; width:100%; text-align:right; padding-left:10%;"}
          [:input {:type "search" :name "q"
                   :placeholder "Search"
                   :spellcheck false
                   }]]]))
      [:hr]
      body]))

(def vcat (comp vec concat))
