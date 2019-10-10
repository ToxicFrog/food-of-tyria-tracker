(defproject food-of-tyria "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "0.7.6"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [io.aviso/pretty "0.1.37"]
                 [ring-server "0.5.0"]
                 [codax "1.3.1"]]
  :plugins [[lein-ring "0.12.5"]
            [io.aviso/pretty "0.1.37"]]
  :middleware [io.aviso.lein-pretty/inject]
  :ring {:handler food-of-tyria.handler/app
         :init food-of-tyria.handler/init
         :destroy food-of-tyria.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false :port 8099}}
   :dev
   {:dependencies [[ring/ring-mock "0.4.0"] [ring/ring-devel "1.7.1"]]}})
