(ns dry-todo.datascript-entities
  (:require [datascript :as d]
            [datascript.js :as djs]))

(defonce conn (let [conn (d/create-conn {})]
                (d/transact! conn
                             [{:type     :new-todo
                               :text     ""
                               :complete false}
                              {:type        :filter
                               :todo-status :all}])
                conn))

(defn eid->clj [db eid]
  (into {:db/id eid} (d/entity db eid)))

(defn get-all-entities []
  (map (comp (partial eid->clj @conn) first)
       (d/q '[:find ?e
              :where [?e _ _]]
            @conn)))

(defn get-entities-by-type [db entity-type]
  (map (comp (partial eid->clj db) first)
       (d/q '[:find ?e
              :in $ ?t
              :where [?e :type ?t]]
            db
            entity-type)))

(defn get-todos []
  (get-entities-by-type @conn :todo))

(defn get-filtered-todos [todo-filter]
  (let [todos (get-entities-by-type @conn :todo)]
    (filter (fn [todo]
              (case (:todo-status todo-filter)
                :incomplete (not (:complete todo))
                :complete (:complete todo)
                :all true))
            todos)))

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

(defn get-filter []
  (first (get-entities-by-type @conn :filter)))

(defn remove-entity! [entity]
  (d/transact! conn [[:db.fn/retractEntity (:db/id entity)]]))

(defn update-entity! [updated-entity-data]
  (d/transact! conn [updated-entity-data]))

(defn add-entity! [entity-data entity-type]
  (d/transact! conn
               [(merge entity-data {:type  entity-type
                                    :db/id nil})]))
