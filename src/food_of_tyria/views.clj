(ns food-of-tyria.views
  (:require [hiccup.page :refer [html5 include-css]]
            [food-of-tyria.models.recipes :as recipes]))

(defn- page [& body]
  (html5
    [:head
     [:title "Welcome to food-of-tyria"]
     (include-css "/css/screen.css")]
    [:body body]))

(defn- item-tr [item]
  [:tr
   [:td [:img {:src (item :icon)
               :title (item :description)}]]
   [:td {:align "right"} [:b (item :count 1)]]
   [:td
    (if (item :ingredients)
      [:a {:href (str "/items/" (item :id))} (item :name)]
      (item :name))]])

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
      [:h1
       [:img {:src (item :icon)}]
       (item :name)
       [:img {:src (item :icon)}]]
      [:div {:align "center" :width "50%"}
       [:i (item :type)]
       " [" [:a {:href (str "https://api.guildwars2.com/v2/items/" id)} "item"]
       " " [:a {:href (str "https://api.guildwars2.com/v2/recipes/" (item :recipe-id))} "recipe"]
       "]"
       [:hr]
       (if (item :ingredients)
         (vec (concat [:table] (mapv item-tr (item :ingredients)))))]
      )))

(defn recipes-page [type]
  (->> (recipes/get-recipes)
       (filter #(= type (:type %)))
       (sort-by :skill)
       (mapv item-tr)
       (concat [:table])
       (vec)
       (page)))

