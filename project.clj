(defproject clostar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :ring {:handler clostar.web/app}
  :profiles
    {:dev
      {:dependencies [[midje "1.5.0"]
                      [ring-mock "0.1.2"]]
       :plugins [[lein-midje "3.0.1"]]}}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [congomongo "0.4.1"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [tentacles "0.2.4"]]
  :plugins [[lein-ring "0.8.2"]])
