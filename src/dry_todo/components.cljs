(ns ^:figwheel-always dry-todo.components
  (:require [dry-todo.datascript-entities :refer [get-new-todo get-todos]]
            [rum]
            [dry-todo.actions :refer [component-for-action]]))

(rum/defc new-todo-form [new-todo]
          [:form {:key 1}
           (component-for-action :fill-in-new-todo new-todo)
           (component-for-action :add-new-todo new-todo)])


(rum/defc todo-list [todos]
          [:ul
           {:key 0}
           (for [todo todos]
             [:li
              {:key (:db/id todo)}

              (component-for-action :toggle-todo-complete todo)

              [:span {:style {:margin          10
                              :text-decoration (if (:complete todo)
                                                 "line-through")}}
               (:text todo)]

              [:span (component-for-action :remove-todo todo)]])])


(rum/defc todo-app []
          [:main
           (new-todo-form (get-new-todo))
           (todo-list (get-todos))])
