(ns clostar.storage
  (:require [somnium.congomongo :as m]
            [clojure.tools.logging :as log]))

(m/set-connection!
  (m/make-connection (or (System/getenv "MONGODB_URL") "mongodb://127.0.0.1:17017/clostar")))

(defn insert-event
  "Inserts a watch event into Mongo."
  [event]
  (let [doc {:r (:name (:repo event))
            :u (:login (:actor event))}]
    (try
      (m/insert! :events doc)
      (log/info "inserting:" doc)
      (catch com.mongodb.MongoException$DuplicateKey e nil))))

(defn find-events
  "Finds events ordered by creation time."
  []
  (m/fetch :events :sort {:_id -1}))

(defn add-indexes
  "Add indexes for MongoDB."
  []
  (m/add-index! :events [:r :u] :unique true))
