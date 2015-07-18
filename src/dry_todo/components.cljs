(ns ^:figwheel-always dry-todo.components
  (:require [dry-todo.datascript-entities :refer [get-new-todo get-todos]]
            [rum]
            [dry-todo.actions :refer [component-for-action]]))

(defn new-todo-form [new-todo]
  [:form {:key 1}
   (component-for-action :fill-in-new-todo new-todo)
   (component-for-action :add-new-todo new-todo)
   ])

(rum/defc todo-item [todo]
          [:li
           {:key (:db/id todo)}
           (component-for-action :toggle-todo-complete todo)
           [:span {:style {:margin 10}}
            (:text todo)]
           [:span (component-for-action :remove-todo todo)]])

(rum/defc todo-list [todos]
          [:ul
           {:key 0}
           (for [todo todos]
             (todo-item todo))])


(rum/defc todo-app []
          [:main {:key 0}
           (new-todo-form (get-new-todo))
           (todo-list (get-todos))])
