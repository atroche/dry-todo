(ns ^:figwheel-always dry-todo.action-components
  (:require [rum]))

(rum/defc
  add-todo-button [todo event-handlers]
  [:button event-handlers
   "Add Todo"])

(rum/defc
  new-todo-text-input [new-todo event-handlers]
  [:input (merge {:type  "text"
                  :value [(:text new-todo)]}
                 event-handlers)])

(rum/defc
  remove-todo-button [todo event-handlers]
  [:button event-handlers
   "x"])

(rum/defc
  todo-complete-checkbox [todo event-handlers]
  [:input (merge {:type    :checkbox
                  :checked (:complete todo)}
                 event-handlers)])

(rum/defc
  choose-filter-component [filter-type current-filter event-handlers]
  [:li {:key   "incomplete-filter"
        :style (merge {:padding 2
                       :margin  2}
                      (if (= filter-type (:todo-status current-filter))
                        {:border-color "rgba(175, 47, 47, 0.2)"
                         :border "1px solid"
                         :border-radius 3}
                        nil))}
   [:a (merge {:style {:text-decoration nil}}
              event-handlers)
    (name filter-type)]])

(defn choose-filter [filter-type]
  (fn [current-filter event-handlers]
    (choose-filter-component filter-type current-filter event-handlers)))