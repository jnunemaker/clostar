(ns clostar.storage
  (:require [somnium.congomongo :as m]))

(m/set-connection!
  (m/make-connection (or (System/getenv "MONGODB_URL") "mongodb://127.0.0.1:17017/clostar")))

(defn insert-event
  "Inserts a watch event into Mongo."
  [event]
  (let [doc {:r (:name (:repo event))
            :u (:login (:actor event))}]
    (try
      (m/insert! :stars doc)
      (catch com.mongodb.MongoException$DuplicateKey e nil))))

(defn find-events
  "Finds events ordered by creation time."
  []
  (m/fetch :stars :sort {:_id -1}))

(defn add-indexes
  "Add indexes for MongoDB."
  []
  (m/add-index! :stars [:r :u] :unique true))
