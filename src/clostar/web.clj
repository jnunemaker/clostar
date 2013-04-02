(ns clostar.web
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [clostar.storage :as storage]
            [clostar.poller :as poller]
            [clostar.core :as core]
            [clojure.java.io :as io]
            [clostache.parser :as tpl]))

(defn- resource
  "Find a relative path under project resources/ dir as a resource and
   return the full path."
  [path]
  (when path
    (-> (Thread/currentThread) .getContextClassLoader (.getResource path))))

(defn- slurp-template
  "Load the mustache template under resources/. You don't have to specify the extension.
   (slurp-template \"views/myview\") loads the contents of resources/views/myview.mustache."
  [path]
  (let [res (resource (str path ".mustache"))]
    (when res
        (slurp (io/file res)))))

(defn formatted-events
  "Returns a list of star events from Mongo as HTML."
  []
  (tpl/render (slurp-template "stars") {:events (storage/find-events)}))

(defn index
  "Show me the goodness."
  [request]
  {:status 200 :body (formatted-events)})

(defroutes app-routes
  (GET "/" request (index request)))

(future
  (poller/poll (or (System/getenv "CLOSTAR_USERNAME") "jnunemaker") 300 storage/insert-event))

(core/ping-on-interval)

(def app (handler/site app-routes))
