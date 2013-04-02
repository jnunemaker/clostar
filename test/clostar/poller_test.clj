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
   {:id "2" :type "FollowEvent"}
   {:id "3" :type "SomethingEvent"}
   {:id "5" :type "WatchEvent"}
   {:id "6" :type "FooEvent"}])

(fact "when the events API returns a list of events"
  (prerequisite (events/user-events "username") => fixture)

  (fact "get-events returns the full list"
    (poller/get-events "username") => fixture)

  (fact "get-watches returns only WatchEvents"
    (poller/get-watches "username") => (just (first fixture))))

(def test-sequence [fixture later-events])

(def callback-events (atom []))
(defn callback [event] (swap! callback-events (fn [events] (conj events event))))

(fact "consume-with-delay takes a sequence of GitHub events and returns unique
      events in increasing order by id"
  (#'poller/consume-with-delay test-sequence 0.001 callback) => nil
  (map #(:id %1) @callback-events) => (just ["1" "2" "3" "4" "5" "6"]))

  (fact "poll hits the API until an exception is thrown"
    (poller/poll "username" 0.001 identity) => (throws RuntimeException)
    (provided (poller/get-events anything) =throws=> (RuntimeException.)))