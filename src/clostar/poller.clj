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

(defn- consume-with-delay
  "Consume groups of GitHub Watch events as a sequence, delaying interval seconds
   between consuming each event. When events with higher ids than any previously
   seen event are encountered, call the collector-fn on them."
  [github-events interval collector-fn]
  (let [last-id (atom 0)]
    (loop [events github-events]
      (when-not (empty? events)
        (let [watch-events (first events)
              max-id (reduce max-event-id 0 watch-events)
              unseen-watches (filter #(> (event-id %1) @last-id) watch-events)]
          (dorun (map collector-fn unseen-watches))
          (reset! last-id max-id)
          (Thread/sleep (* 1000 interval))
          (recur (rest events)))))))

(defn poll
  "Poll for watch events for a user every interval seconds and call a collector
  function on each new event."
  [username interval collector-fn]
  (let [github-events (repeatedly (get-watches username))]
    (consume-with-delay github-events interval collector-fn)))

