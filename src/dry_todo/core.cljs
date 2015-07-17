(ns ^:figwheel-always dry-todo.core
  (:require [reagent.core :refer [render-component]]
            [dry-todo.components :refer [todo-app]]))

(enable-console-print!)

(def root-component (render-component [todo-app] (. js/document (getElementById "app"))))

