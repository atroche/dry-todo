(ns dry-todo.transforms
  (:require [dry-todo.entities :refer [new-entity]]))

(defn create-todo [text]
  {:text     text
   :complete false})

(defn change-text [new-todo new-todo-text]
  (assoc new-todo :text new-todo-text))

(defn wipe-out-text [new-todo _]
  (assoc new-todo :text ""))

(defn add-new-todo-entity [entities new-todo]
  (conj entities (new-entity :todo (create-todo (:text new-todo)))))
