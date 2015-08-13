(ns dry-todo.transforms)


(defn change-text [new-todo new-todo-text]
  (assoc new-todo :text new-todo-text))

(defn wipe-out-text [new-todo _]
  (assoc new-todo :text ""))


(defn toggle-complete [todo _]
  (assoc todo
    :complete
    (not (:complete todo))))
