(ns dry-todo.components
  (:require [dry-todo.datascript-entities :refer [get-new-todo get-todos]]
            [rum]
            [dry-todo.actions :refer [component-for-action]]))

(defn new-todo-form [new-todo]
  [:form
   (component-for-action :fill-in-new-todo new-todo)
   (component-for-action :add-new-todo new-todo)])


(rum/defc todo-list [todos]
          [:ul
           (for [todo todos]
             [:li
              {:key (:db/id todo)}
              [:span {:style {:margin 10}}
               (:text todo)]
              [:span (component-for-action :remove-todo todo)]])])


(rum/defc todo-app [db]
          [:main
           (new-todo-form (get-new-todo db))
           (todo-list (get-todos db))])

