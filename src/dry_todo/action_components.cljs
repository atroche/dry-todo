(ns dry-todo.action-components)

(defn add-todo-button [_ event-handlers]
  [:button event-handlers
   "Add Todo"])

(defn new-todo-text-input [new-todo event-handlers]
  [:input (merge {:type  "text"
                  :value [(:text new-todo)]}
                 event-handlers)])

(defn remove-todo-button [_ event-handlers]
  [:button event-handlers
   "x"])