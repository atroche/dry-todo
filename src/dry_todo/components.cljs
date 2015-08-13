(ns ^:figwheel-always dry-todo.components
  (:require [dry-todo.datascript-entities :refer [get-new-todo get-todos
                                                  get-incomplete-todos
                                                  get-filter
                                                  get-filtered-todos]]
            [rum]
            [dry-todo.actions :refer [component-for]]))

(rum/defc
  new-todo-form [new-todo]
  [:form
   (component-for :fill-in-new-todo new-todo)
   (component-for :add-new-todo new-todo)])

(rum/defc
  todo-item [todo]
  [:li
   {:key (:db/id todo)}

   (component-for :toggle-todo-complete todo)

   [:span {:style {:margin          10
                   :text-decoration (if (:complete todo)
                                      "line-through")}}
    (:text todo)]

   [:span
    (component-for :remove-todo todo)]])

(rum/defc
  todo-list [todos]
  [:ul
   (for [todo todos]
     (todo-item todo))])

(rum/defc
  remaining-todos-count []
  [:span (str (count (get-incomplete-todos)) " items left")])

(rum/defc
  todo-filters [current-filter]
  [:div
   {:style {:margin-top 20
            :width 150}}
   "Filter:"
   [:ul
    (for [filter-status [:choose-all-filter
                         :choose-incomplete-filter
                         :choose-complete-filter]]
      (component-for filter-status current-filter))]])

(rum/defc
  todo-app []
  (let [current-filter (get-filter)
        todos (get-filtered-todos current-filter)]
    (println current-filter)
    [:main
     (new-todo-form (get-new-todo))

     (todo-list todos)

     [:footer
      (remaining-todos-count)

      (todo-filters current-filter)]]))
