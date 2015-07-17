(ns dry-todo.components
  (:require [dry-todo.entities :refer [entities-with-type add-entity! update-entity! entities]]
            [reagent.impl.util :as util]
            [dry-todo.actions :refer [component-for-action]]))

(defn new-todo-form []
  (let [new-todo (first (entities-with-type @entities :new-todo))]
    [:form
     (component-for-action :fill-in-new-todo new-todo)
     (component-for-action :add-new-todo new-todo)]))


(defn todo-list []
  [:ul
   (for [todo (entities-with-type @entities :todo)]
     [:li {:key (:id todo)} (:text todo)])])

;(defn todo-app []
;  [:main
;   [new-todo-form]
;   [todo-list]])

(def todo-app
  (with-meta (fn []
               [:main
                [new-todo-form]
                [todo-list]])
             {:component-did-mount
              (fn [this]
                (def c this))}))