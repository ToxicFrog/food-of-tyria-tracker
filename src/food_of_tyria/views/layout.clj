(ns food-of-tyria.views.layout
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

(defn item-page [id]
  (let [item (recipes/get-item id)]
    (page
      [:h1
       [:img {:src (item :icon)}]
       (item :name)
       [:img {:src (item :icon)}]]
      [:div {:align "center" :width "50%"}
       [:h2 (item :type)]
       [:hr]
       (if (item :ingredients)
         (vec (concat [:table] (mapv item-tr (item :ingredients)))))]
      )))

(defn recipes-page []
  (->> (recipes/get-recipes)
       (mapv item-tr)
       (concat [:table])
       (vec)
       (page)))
