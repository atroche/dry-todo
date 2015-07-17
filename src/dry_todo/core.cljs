(ns ^:figwheel-always dry-todo.core
  (:require [reagent.core :as reagent :refer [atom render-component]]
            [dry-todo.components :refer [todo-app]]
            [dry-todo.entities :refer [entities]]))

(enable-console-print!)


(render-component [todo-app]
                  (. js/document (getElementById "app")))

