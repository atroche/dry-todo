(ns ^:figwheel-always dry-todo.action-components
  (:require [rum]))

(rum/defc add-todo-button [todo event-handlers]
          [:button (merge {:key (:db/id todo)} event-handlers)
           "Add Todo"])

(rum/defc new-todo-text-input [new-todo event-handlers]
          [:input (merge {:type  "text"
                          :key 0
                          :value [(:text new-todo)]}
                         event-handlers)])

(rum/defc remove-todo-button [todo event-handlers]
          [:button (merge {:key (:db/id todo)} event-handlers)
           "x"])

(rum/defc todo-complete-checkbox [todo event-handlers]
          (println "this thing is updating!")
          (print todo)
          [:input (merge {:type :checkbox
                          :key (:db/id todo)
                          :checked (:complete todo)}
                         event-handlers)])

