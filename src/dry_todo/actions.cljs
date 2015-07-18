(ns dry-todo.actions
  (:require [dry-todo.action-components :refer [new-todo-text-input add-todo-button
                                                remove-todo-button]]
            [dry-todo.datascript-entities :refer [update-entity! add-entity! remove-entity!]]
            [dry-todo.transforms :refer [change-text wipe-out-text]]))



(def todo-actions
  {:fill-in-new-todo {:component        new-todo-text-input
                      :event-type       :on-change
                      :entity-transform change-text}

   :add-new-todo     {:component         add-todo-button
                      :entity-type       :todo
                      :event-type        :on-click
                      :entity-transform  wipe-out-text
                      :possibility-check (fn [new-todo] (not (empty? (:text new-todo))))
                      :add-entity?       true}

   :remove-todo      {:component      remove-todo-button
                      :entity-type    :todo
                      :event-type     :on-click
                      :remove-entity? true}})


(defn event-handlers-for-action [action-name entity]
  (let [{:keys [event-type entity-transform remove-entity?
                add-entity? entity-type possibility-check]} (action-name todo-actions)]
    {event-type (fn [e]
                  (when (or (nil? possibility-check) (possibility-check entity))
                    (let [event-value (.-value (.-target e))]
                      (when entity-transform
                        (update-entity! (entity-transform entity event-value)))
                      (when remove-entity?
                        (remove-entity! entity))
                      (when add-entity?
                        (add-entity! (merge entity {:type  entity-type
                                                    :db/id nil})))))
                  (.preventDefault e))}))

(defn component-for-action [action-name entity]
  (let [action (action-name todo-actions)]
    ((:component action) entity (event-handlers-for-action action-name entity))))
