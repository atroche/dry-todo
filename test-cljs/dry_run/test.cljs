(ns dry-run.test
  (:require [dry-todo.entities :refer [entities]]
            [dry-todo.actions :refer [todo-actions]]))

(defn possible-actions [entity]
  (filter (fn [action]
            (let [{:keys [entity-type possible?]} action]
              (and (= entity-type (:type entity))
                   (possible? entity))))
          (vals todo-actions)))

(def entity (first @entities))

; if it's on-change, provide extra arg, otherwise nothing
(for [[action-name {:keys [event-type entity-type possible?]}] todo-actions
      :when (possible? entity)]
  (if (and (= entity-type (:type entity))
           (possible? entity))
    (if (= event-type :on-change)
      [action-name entity "hello this is a random string"]
      [action-name entity])))

[:fill-in entity "asdfasdf"] [:add-new entity]

todo-actions

(second (first todo-actions))

(first @entities)

(defn apply-action [entity action]
  ((:transform action) entity))

(let [entity (first @entities)]
  (println entity)
  (apply-action entity (first (possible-actions entity))))

(defn perform-action [[action-name entity value]]
  (let [{:keys [transform]} (action-name todo-actions)]
    (transform entity)))

