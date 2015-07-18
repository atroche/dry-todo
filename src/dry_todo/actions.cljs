(ns dry-todo.actions
  (:require [dry-todo.action-components :refer [new-todo-text-input add-todo-button]]
            [dry-todo.datascript-entities :refer [update-entity! add-entity!]]
            [dry-todo.transforms :refer [change-text wipe-out-text]]))



(def todo-actions
  {:fill-in-new-todo {:component        new-todo-text-input
                      :event-type       :on-change
                      :entity-transform change-text}

   :add-new-todo     {:component        add-todo-button
                      :entity-type :todo
                      :event-type       :on-click
                      :entity-transform wipe-out-text
                      :add-entity? true}})


(defn event-handlers-for-action [action-name entity]
  (let [{:keys [event-type entity-transform
                add-entity? entity-type]} (action-name todo-actions)]
    {event-type (fn [e]
                  (let [event-value (.-value (.-target e))]
                    (update-entity! (entity-transform entity event-value))
                    (when add-entity?
                      (add-entity! (merge entity {:type entity-type
                                                  :db/id nil}))))
                  (.preventDefault e))}))

(defn component-for-action [action-name entity]
  (let [action (action-name todo-actions)]
    ((:component action) entity (event-handlers-for-action action-name entity))))
