(ns food-of-tyria.views.common
  (:require
    [hiccup.page :refer [html5 include-css include-js]]
    [food-of-tyria.models.recipes :as recipes]))

(defn- ingredient-cell [{id :id :as item} opts]
  [:td.ingredient-cell (apply assoc {} nil nil opts)
   [:a {:href (str "/ingredient/" id)}
    [:img {:style "vertical-align:middle;" :src (item :icon) :title (item :description)}]]
   " "
   (if (item :count)
     [:b (item :count) " "]
     "")
   (item :name)])

(defn- recipe-cell [{id :id :as item} opts]
  [:td.recipe-cell (apply assoc {} nil nil opts)
   [:a {:href (str "/ingredient/" id)}
    [:img {:style "vertical-align:bottom;" :src (item :icon) :title (item :description)}]]
   [:div {:style "display:inline-block;"}
    (if (item :count)
      [:b (item :count) " "]
      "")
    [:a {:href (str "/items/" (item :id)) :id (str "label:" (item :id))}
     (item :name)]
    [:br]
    [:span {:id (str "button:" id)}
     [:input {:type "checkbox" :checked (item :cooked)
              :id (str id) :class "cooked-toggle"
              :onchange "toggleCooked(this);"}
      "🍴"]
     (if (not (recipes/prerequisites-cooked? id))
       [:b {:style "color:#F00;"} "    ⚠"])]
    [:br] "&nbsp;"
    ]])

(defn item-cell [item & opts]
  (if (item :ingredients)
    (recipe-cell item opts)
    (ingredient-cell item opts)))

(defn page-header [{id :id icon :icon :as item} subheading]
  [:table.page-header
   [:tr
    [:td [:a {:href (str "/ingredient/" id)} [:img {:src icon}]]]
    [:td {:style "vertical-align:middle; text-align:center;"}
     [:h1 {:style "margin:0px;" :id (str "label:" id)} (item :name)]
     subheading
     ; " [API " [:a {:href (str "https://api.guildwars2.com/v2/items/" id)} "⏣item"]
     ; (if (item :recipe-id)
     ;   [:span " " [:a {:href (str "https://api.guildwars2.com/v2/recipes/" (item :recipe-id))} "⚙recipe"]]
     ;   "")
     ; "]"
     ]
    [:td [:a {:href (str "/ingredient/" id)} [:img {:src icon}]]]]])

(defn difficulty-to-tier [rating]
  (condp > (or rating -1)
      0 "Ingredient"
     75 "Novice"
    150 "Initiate"
    225 "Apprentice"
    300 "Journeyman"
    400 "Adept"
    500 "Master"
    999 "Grandmaster"))

(defn progress-bar [type]
  (let [[cooked total] (recipes/cooked-percentage type)
        percent (quot (* 100 cooked) total)
        cls (condp >= (quot (* 100 cooked) total)
                0 "not-started"
               33 "low-progress"
               66 "medium-progress"
               99 "high-progress"
              100 "completed")]
    [:td
     [:a {:href (str "/recipes/" type)} type]
     "&nbsp;(" percent "%)"
     [:br]
     [:progress {:class cls :min 0 :max total :value cooked}]]))

(defn type-list []
  (->> (recipes/get-recipes)
       (map :type)
       (distinct)
       (sort)
       (map progress-bar)))

(def vcat (comp vec concat))

(defn page [& body]
  (html5
    [:head
     [:title "Food of Tyria"]
     (include-css "/css/screen.css")
     (include-js "/js/tyria.js")]
    [:body {:onload "updateAllStyles();"}
     [:table.nav-header
      (vcat
        [:tr]
        (type-list)
        [[:td {:style "width:100%;"}]
         [:td
          [:form {:action "/search" :method "get" }
           [:input {:type "search" :name "q"
                    :placeholder "Search"
                    :spellcheck false
                    }]]]])]
     [:hr]
     body]))

(def vcat (comp vec concat))
