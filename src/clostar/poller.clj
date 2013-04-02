(ns clostar.poller
  (:require [tentacles.events :as events]
            [clojure.tools.logging :as log]))

(defn get-events
  "Get all events for a user."
  [username]
  (events/user-events username))

(defn get-watches
  "Get all watch events for a user."
  [username]
  (filter #(= "WatchEvent" (:type %1)) (get-events username)))

(defn- event-id
  [event]
  (Long/parseLong (:id event)))

(defn- max-event-id
  [current-max-id next-event]
  (max current-max-id (event-id next-event)))

(defn poll
  "Poll for watch events for a user every interval seconds and call a collector
  function on each new event."
  [username interval collector-fn]
  (let [last-id (atom 0)]
    (loop []
      (log/info "Fetching watches!")
      (let [watch-events (get-watches username)
            max-id (reduce max-event-id 0 watch-events)
            unseen-watches (filter #(> (event-id %1) @last-id) watch-events)]
        (dorun (map collector-fn unseen-watches))
        (reset! last-id max-id)
        (Thread/sleep (* 1000 interval))
        (recur)))))
