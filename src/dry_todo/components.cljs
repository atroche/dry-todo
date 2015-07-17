(ns dry-todo.components
  (:require [dry-todo.entities :refer [entities-with-type add-entity! update-entity! entities]]))


(defn create-todo [text]
  {:text     text
   :complete false
   :type     :todo})


(defn add-todo-button [_ event-handlers]
  [:button event-handlers
   "Add Todo"])

(defn new-todo-text-input [new-todo event-handlers]
  [:input (merge {:type  "text"
                  :value [(:text new-todo)]}
                 event-handlers)])


(def todo-actions
  {:fill-in-new-todo {:component  new-todo-text-input
                      :event-type :on-change
                      :transform  (fn [new-todo new-todo-text]
                                    (assoc new-todo :text new-todo-text))}
   :add-new-todo     {:component         add-todo-button
                      :event-type        :on-click
                      :global-transform (fn [new-todo]
                                           (add-entity! (create-todo (:text new-todo))))
                      :transform         (fn [new-todo _]
                                           (assoc new-todo :text ""))}})


(defn event-handlers-for-action [action-name entity]
  (let [{:keys [event-type transform global-transform]} (action-name todo-actions)]
    {event-type (fn [e]
                  (let [event-value (.-value (.-target e))]
                    (update-entity! entity transform event-value)
                    (when global-transform
                      (global-transform entity)))
                  (.preventDefault e))}))

(defn component-for-action [action-name entity]
  (let [action (action-name todo-actions)]
    ((:component action) entity (event-handlers-for-action action-name entity))))



(defn new-todo-form []
  (let [new-todo (first (entities-with-type @entities :new-todo))]
    [:form
     (component-for-action :fill-in-new-todo new-todo)
     (component-for-action :add-new-todo new-todo)]))


(defn todo-list []
  [:ul
   (for [todo (entities-with-type @entities :todo)]
     [:li {:key (:id todo)} (:text todo)])])

(defn todo-app []
  [:main
   [new-todo-form]
   [todo-list]])

