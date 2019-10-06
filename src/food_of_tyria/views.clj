(ns food-of-tyria.views
  (:require [hiccup.page :refer [html5 include-css]]
            [food-of-tyria.models.recipes :as recipes]))

(defn- page [& body]
  (println (first body))
  (html5
    [:head
     [:title "Welcome to food-of-tyria"]
     (include-css "/css/screen.css")]
    [:body body]))

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
      [:td] [:td]
      [:td (ingredient-table (item :ingredients))]]
     "")])

(def vcat (comp vec concat))

(defn- ingredient-table [ingredients]
  (vcat [:table]
        (mapcat item-tr ingredients)))

(defn type-list []
  (->> (recipes/get-recipes)
       (map :type)
       (distinct)
       (sort)
       (mapcat (fn [type] [[:a {:href (str "/recipes/" type)} type] [:br]]))
       (apply page)))

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (page
      [:table [:tr {:style "vertical-align:middle;"}
      [:h1
       [:img {:src (item :icon)}]
       " " (item :name) " "
       [:img {:src (item :icon)}]]
      ]]
      [:div {:align "center" :width "50%"}
       [:i (item :type)]
       " [" [:a {:href (str "https://api.guildwars2.com/v2/items/" id)} "item"]
       " " [:a {:href (str "https://api.guildwars2.com/v2/recipes/" (item :recipe-id))} "recipe"]
       "]"
       (if (item :name)
         [:span {:style "font-weight:bold; text-shadow:0 0 5px #F00"} [:input {:type "checkbox"}] " 🍴"]
         " 🍴")
       [:hr]
       (if (item :ingredients)
         (ingredient-table (item :ingredients)))])))

(defn- recipe-link [item]
  [:tr
   [:td [:img {:src (item :icon)
               :title (item :description)}]]
   [:td {:align "right"} [:b (item :count 1)]]
   [:td [:a {:href (str "/items/" (item :id))} (item :name)]]])

(defn- difficulty-to-tier [rating]
  (condp > rating
     75 "Novice"
    150 "Initiate"
    225 "Apprentice"
    300 "Journeyman"
    400 "Adept"
    500 "Master"
    999 "Grandmaster"))

(defn- recipe-table [recipes]
  (vcat
    [:table
     [:tr
      [:th {:colspan 3} (-> recipes first :skill difficulty-to-tier)]]]
    (mapv recipe-link recipes)))

(defn recipes-page [type]
  (page
    [:div {:width "50%" :align "center"}
     (->> (recipes/get-recipes)
          (filter #(= type (:type %)))
          (sort-by :skill)
          (partition-by #(difficulty-to-tier (% :skill)))
          (map recipe-table))]))
