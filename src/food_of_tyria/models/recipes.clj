(ns food-of-tyria.models.recipes
  "Data model for recipes and items.
  Items are simple; we store {:id :name :description :icon}.
  Recipes we partially merge with the item they produce, adding the keys
  {:recipe-id :skill :type :ingredients}.
  :ingredients is a vec of {:id :count}.
  "
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
  (and
    (some #{"Chef"} (recipe :disciplines))
    (not (contains?
           #{"Backpack" "Dye" "Feast" "Refinement" "RefinementObsidian" "Consumable" "Component"}
           (recipe :type)))))

(defn- fetch-and-store
  "Fetch an item by ID and store the result in the database. If already in the
  database, fetches nothing and returns the stored item. Items are pruned down
  to just the fields we care about."
  [id]
  (c/update-at!
    db [:items id]
    (fn [item]
      (if (nil? item)
        (-> (str "https://api.guildwars2.com/v2/items/" id)
            http/get :body
            (json/read-str :key-fn keyword)
            (select-keys [:name :id :icon :description]))
        item))))

(defn- cook-and-store
  "Given a raw recipe returned from the GW2 API, fetch the item definitions for
  its output item and all input items into the database, and annotate the output
  item with the recipe."
  [recipe]
  ; Fetch and store all the ingredients of the recipe.
  (->> (recipe :ingredients)
       (map :item_id)
       ; Skips items already in the DB, so this won't repeatedly re-fetch things.
       (run! fetch-and-store))
  ; Fetch the output item and annotate it with the recipe.
  (let [output (assoc
                 (fetch-and-store (recipe :output_item_id))
                 :recipe-id (recipe :id)
                 :count (recipe :output_item_count)
                 :skill (recipe :min_rating)
                 :type (recipe :type)
                 :ingredients (mapv (fn [i]
                                      {:id (i :item_id) :count (i :count)})
                                    (recipe :ingredients)))]
    (println "COOK" (output :name))
    (c/assoc-at! db [:items (output :id)] output)
    ))

(defn init []
  (if (not (c/get-at! db [:initialized?]))
    (do
      (println "Database not initialized, fetching recipes from GW2...")
      (as-> (http/get "https://api.guildwars2.com/v2/recipes") $
            (:body $)
            (json/read-str $ :key-fn keyword)
            (partition-all 30 $)
            (mapcat fetch-recipes $)
            (filter cookable? $)
            (run! cook-and-store $))
      (c/assoc-at! db [:initialized?] true))))

(defn deinit [] (c/close-database! db))

(defn get-item [id]
  "Get an item from the database. If it has a recipe, the ingredients for the
  recipe will be recursively merged with the items they reference."
  (let [item (c/get-at! db [:items id])
        reify-ingredient (fn [i] (merge (get-item (i :id)) i))]
    (if (item :ingredients)
      (update-in item [:ingredients] (partial mapv reify-ingredient))
      item)))

(defn get-recipes []
  (->> (c/seek-at! db [:items])
       (map second)
       (filter :ingredients)))

(defn set-cooked! [id state]
  (c/update-at!
    db [:items id]
    (fn [item] (assoc item :cooked state))))

(defn cooked-percentage [category]
  (let [recipes (filter #(= (% :type) category) (get-recipes))
        total (count recipes)
        cooked (count (filter :cooked recipes))]
    (str (quot (* 100 cooked) total) "%")))

(defn- item-texts [item]
  (->> (tree-seq :ingredients :ingredients item)
       (map :name)
       (map string/lower-case)))

(defn- matches? [query item]
  (println "matches?" query)
  (println "matches:" (vec (item-texts item)))
  (every?
    (fn [query-word] (some #(string/includes? % query-word) (item-texts item)))
    query))

(defn search
  "Return all ingredients and recipes that match a search query, which is a set of strings. An ingredient or recipe is considered to 'match' if all search terms appear somewhere in (name of recipe) U (list of recipe ingredient names)."
  [query]
  (println "query: " query)
  (->> (c/seek-at! db [:items])
       (map first)
       (map get-item)
       (filter #(matches? query %))))
