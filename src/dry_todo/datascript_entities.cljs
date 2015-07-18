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

(defn get-todos []
  (get-entities-by-type @conn :todo))

(defn get-incomplete-todos []
  (map (comp (partial eid->clj @conn) first)
       (d/q '[:find ?e
              :in $ ?t ?c
              :where [?e :type ?t]
                     [?e :complete ?c]]
            @conn
            :todo
            false)))

(defn get-new-todo []
  (first (get-entities-by-type @conn :new-todo)))

(defn remove-entity! [entity]
  (d/transact! conn [[:db.fn/retractEntity (:db/id entity)]]))

(defn update-entity! [updated-entity-data]
  (d/transact! conn [updated-entity-data]))

(defn add-entity! [entity-data]
  (d/transact! conn [entity-data]))
