(ns dry-todo.datascript-entities
  (:require [datascript :as d]
            [datascript.js :as djs]))

(defonce conn (let [conn (d/create-conn {})]
                (d/transact! conn
                             [{:type     :new-todo
                               :text     ""
                               :complete false}])
                conn))

(defn eid->clj [db eid]
  (into {:db/id eid} (d/entity db eid)))


(defn get-entities-by-type [db entity-type]
  (map (comp (partial eid->clj db) first)
       (d/q '[:find ?e
              :in $ ?t
              :where [?e :type ?t]]
            db
            entity-type)))

(defn get-todos [db]
  (get-entities-by-type db :todo))

(defn get-new-todo [db]
  (first (get-entities-by-type db :new-todo)))

; use cases for modifying:
; adding todo
; changing new-todo

(defn create-todo [text]
  {:text     text
   :complete false})


(defn update-entity! [updated-entity-data]
  (d/transact! conn [updated-entity-data]))

(defn add-entity! [entity-data]
  (d/transact! conn [entity-data]))
