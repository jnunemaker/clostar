(ns clostar.poller-test
  (:require [midje.sweet :refer :all]
            [clostar.poller :as poller]
            [tentacles.events :as events]))

(def fixture
  [{:id "1" :type "WatchEvent"}
   {:id "2" :type "FollowEvent"}
   {:id "3" :type "SomethingEvent"}
   {:id "4" :type "FooEvent"}])

(def later-events
  [{:id "1" :type "WatchEvent"}
   {:id "5" :type "WatchEvent"}])

(fact "when the events API returns a list of events"
  (prerequisite (events/user-events "username") => fixture)

  (fact "get-events returns the full list"
    (poller/get-events "username") => fixture)

  (fact "get-watches returns only WatchEvents"
    (poller/get-watches "username") => (just (first fixture))))

(defn loop-recur [] nil)

(defn events-then-throw
  [iterations]
  (let [times-called (atom 0)]
    (fn [username]
      (if (> @times-called iterations)
        (throw (RuntimeException. "Time's up!"))
        (do
          (loop-recur)
          (swap! times-called inc)
          fixture)))))

(with-redefs [poller/get-events (events-then-throw 5)]
  (fact "poll hits the API periodically until an exception is thrown"
    (poller/poll "username" 0.001 identity) => (throws RuntimeException)
    (provided
      (loop-recur) => nil :times 6)))

(def callback-events (atom []))
(defn callback [event] (swap! callback-events (fn [events] (conj events event))))

(with-redefs [poller/get-events (events-then-throw 1)]
  (fact "poll calls the callback function only for watch events"
    (poller/poll "username" 0.001 callback) => (throws RuntimeException)
    (provided (callback (contains {:id "1" :type "WatchEvent"})) => nil :times 1)))

(defn event-sequence
  []
  (let [iterations (atom 0)]
  (fn [_]
    (swap! iterations inc)
    (condp = @iterations
      1 fixture
      2 later-events
      (throw (RuntimeException. "Time's up!"))))))

(defn callback-event-list [] @callback-events)

(with-redefs [poller/get-events (event-sequence)]
  (with-state-changes [(before :facts (reset! callback-events []))]
    (fact "only events with ids higher
          than the previously highest id will be processed"
      (poller/poll "username" 0.001 callback) => (throws RuntimeException)
      (callback-event-list) => (just [(contains {:id "1"}) (contains {:id "5"})]))))
