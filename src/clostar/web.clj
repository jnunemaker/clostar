(ns clostar.web
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clostar.storage :as storage]
            [clostar.poller :as poller]
            [clostar.core :as core]
            [clojure.java.io :as io]
            [clostache.parser :as tpl]))

(def user (or (System/getenv "CLOSTAR_USERNAME") "jnunemaker"))

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

(def view-defaults
  {:user user})

(def layout-defaults
  {:user user
   :css ["https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css"]
   :js ["https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"]})

(defn render
  "Renders the content in the given mustache template inside the application.mustache
  layout."
  [view & [view-params layout-params]]
  (let [view-content (tpl/render (slurp-template view) (merge view-defaults view-params))
        layout-params (assoc (merge layout-defaults layout-params) :content view-content)]
    {:status 200
     :body (tpl/render (slurp-template "layouts/application") layout-params)}))

(defn- segment-events
  "Split the list of events into a map with two keys:
    :user-events are those WatchEvents where the GitHub API actor is the app user
    :following-events are those from the actor's followers."
  [events]
  (let [group (group-by #(= user (:u %)) events)]
    {:user-events (group true)
    :following-events (group false)}))

(defn index
  "Show me the goodness."
  [request]
  (render "stars" (segment-events (storage/find-events))))

(defroutes app-routes
  (GET "/" request (index request)))

; will check the relative path of requests for static content under public/
(def static-content (route/files "/"))

(def app (handler/site (routes app-routes static-content)))

; polls GitHub API for new star information
(future
  (poller/poll user 300 storage/insert-event))

(core/ping-on-interval)

