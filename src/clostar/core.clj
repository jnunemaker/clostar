(ns clostar.core
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.schedule.simple :refer [schedule repeat-forever with-interval-in-minutes]]))

(defn ping
  "Pings the APP_URL environment variable to keep the app running on Heroku."
  []
  (let [app-url (or (System/getenv "APP_URL") "http://localhost:3000")]
    (try
      (log/info "pinging:" app-url)
      (client/get app-url)
      (catch Exception e :ping-failed))))

(j/defjob PingJob
  [ctx]
  (ping))

(defn ping-on-interval
  "Makes the app ping itself on an interval."
  []
  (qs/initialize)
  (qs/start)
  (let [job (j/build
              (j/of-type PingJob))
        trigger (t/build
                  (t/start-now)
                  (t/with-schedule (schedule
                                     (repeat-forever)
                                     (with-interval-in-minutes 10))))]
    (qs/schedule job trigger)))
