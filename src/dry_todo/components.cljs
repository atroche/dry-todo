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
              (:text todo)])])


(rum/defc todo-app [db]
          [:main {:key 1234}
           (new-todo-form (get-new-todo db))

           (todo-list (get-todos db))])

