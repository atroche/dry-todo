(ns ^:figwheel-always dry-todo.actions
  (:require [dry-todo.action-components :refer [new-todo-text-input add-todo-button
                                                remove-todo-button todo-complete-checkbox]]
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
                      :possibility-check (fn [new-todo]
                                           (not (empty? (:text new-todo))))
                      :add-entity?       true}

   ; holy crap, could you have componenets that represent just an attribute of an entity?
   ; like the span is the text of the todo, the checkbox is its complete status…

   :remove-todo      {:component      remove-todo-button
                      :entity-type    :todo
                      :event-type     :on-click
                      :remove-entity? true}

   :toggle-todo-complete {:component todo-complete-checkbox
                          :entity-type :todo
                          :event-type :on-change
                          :entity-transform  (fn [todo _] (assoc todo :complete (not (:complete todo))))}})


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
  ((:component (action-name todo-actions)) entity (event-handlers-for-action action-name entity)))
