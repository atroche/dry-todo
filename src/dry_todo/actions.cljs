(ns dry-todo.actions
  (:require [dry-todo.action-components :refer [new-todo-text-input add-todo-button]]
            [dry-todo.entities :refer [entities-with-type update-entity! alter-entities!]]
            [dry-todo.transforms :refer [change-text wipe-out-text add-new-todo-entity]]))



(def todo-actions
  {:fill-in-new-todo {:component  new-todo-text-input
                      :entity-type :new-todo
                      :event-type :on-change
                      :possible? (fn [_] true)
                      :transform  change-text}

   :add-new-todo     {:component               add-todo-button
                      :entity-type :new-todo
                      :event-type              :on-click
                      :transform               wipe-out-text
                      :possible? (fn [new-todo] (not (empty? (:text new-todo))))
                      :entities-list-transform add-new-todo-entity}})


(defn event-handlers-for-action [action-name entity]
  (let [{:keys [event-type transform entities-list-transform]} (action-name todo-actions)]
    {event-type (fn [e]
                  (let [event-value (.-value (.-target e))]
                    (update-entity! entity transform event-value)
                    (when entities-list-transform
                      (alter-entities! entities-list-transform entity)))
                  (.preventDefault e))}))

(defn component-for-action [action-name entity]
  (let [action (action-name todo-actions)]
    ((:component action) entity (event-handlers-for-action action-name entity))))
