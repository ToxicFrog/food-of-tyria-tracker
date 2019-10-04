(ns food-of-tyria.models.recipes
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as http])
  (:require [codax.core :as c])
  (:require [clojure.string :as string])
  )

(def db (c/open-database! "food.codax"))

(defn- fetch-recipes [ids]
  (-> (str "https://api.guildwars2.com/v2/recipes?ids=" (string/join "," ids))
      http/get :body
      (json/read-str :key-fn keyword)))

(defn- cookable? [recipe]
  (or
    (->> recipe :disciplines (some #{"Chef"}))
    (->> recipe :type #{"Dessert" "Feast" "IngredientCooking" "Meal" "Seasoning" "Snack" "Soup" "Food"})))

(defn fetch-and-store
  "Fetch an item by ID and store the result in the database. If already in the
  database, fetches nothing and returns the stored item. Items are pruned down
  to just the fields we care about."
  [id]
  (println "fetch-and-store" id)
  (c/update-at!
    db [:items id]
    (fn [item]
      (if (nil? item)
        (-> (str "https://api.guildwars2.com/v2/items/" id)
            http/get :body
            (json/read-str :key-fn keyword)
            (select-keys [:name :id :icon]))
        item))))

(defn- difficulty-to-tier [rating]
  (condp > rating
     75 "Novice"
    150 "Initiate"
    225 "Apprentice"
    300 "Journeyman"
    400 "Adept"
    500 "Master"
    999 "Grandmaster"))

(defn- cook-and-store
  "Given a raw recipe returned from the GW2 API, fetch the item definitions for
  its output item and all input items into the database, and annotate the output
  item with the recipe."
  [recipe]
  (println "cook-and-store" recipe)
  ; Fetch and store all the ingredients of the recipe.
  (->> (recipe :ingredients)
       (map :item_id)
       ; Skips items already in the DB, so this won't repeatedly re-fetch things.
       (map fetch-and-store)
       (map :name)
       (map #(println "ITEM" %))
       dorun)
  ; Fetch the output item and annotate it with the recipe.
  (let [output (fetch-and-store (recipe :output_item_id))
        recipe {:id (recipe :id)
                :type (recipe :type)
                :difficulty (recipe :min_rating)
                :count (recipe :output_item_count)
                :ingredients (recipe :ingredients)}]
    (println "COOK" (output :name))
    (c/assoc-at! db [:items (output :id) :recipe] recipe)
    (c/assoc-at! db [:recipes (recipe :id)] recipe)
    ))

(defn init []
  (if (not (c/get-at! db [:initialized?]))
    (do
      (println "Database not initialized, fetching recipes from GW2...")
      (as-> (http/get "https://api.guildwars2.com/v2/recipes") $
            (:body $)
            (json/read-str $ :key-fn keyword)
            (partition 30 $)
            (mapcat fetch-recipes $)
            (filter cookable? $)
            (take 1 $)
            (do (println "recipes" $) $)
            ; (do (System/exit 0) $)
            ; we are rate limited to 600 requests/minute
            ; unfortunately this pmap does the thing too fast
            ; we can ask for like 30 recipes at a time though
            (run! cook-and-store $))
      (c/assoc-at! db [:initialized?] true))))

(defn deinit [] (c/close-database! db))

(defn get-item [id]
  (let [output (c/get-at! db [:items id])
        reify-item (fn [ingredient] (assoc ingredient :item (get-item (ingredient :item_id))))]
    (update-in
      output [:recipe :ingredients]
      (fn [ingredients] (map reify-item (-> output :recipe :ingredients))))))
