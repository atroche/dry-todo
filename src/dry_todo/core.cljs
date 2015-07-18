(ns ^:figwheel-always dry-todo.core
  (:require [dry-todo.datascript-entities :refer [conn]]
            [dry-todo.components :refer [todo-app]]
            [rum]
            [datascript :as d]
            [clojure.string :as str]))

 (enable-console-print!)

(def root-component (rum/mount (todo-app)
                               (. js/document (getElementById "app"))))

(d/listen! conn
           :render
           (fn [_]
             (rum/request-render root-component)))

(defn on-js-reload []
  (rum/request-render root-component))

(d/listen! conn :log
           (fn [tx-report]
             (let [tx-id  (get-in tx-report [:tempids :db/current-tx])
                   datoms (:tx-data tx-report)
                   datom->str (fn [d] (str (if (.-added d) "+" "−")
                                           "[" (.-e d) " " (.-a d) " " (pr-str (.-v d)) "]"))]
               (println
                 (str/join "\n" (concat [(str "tx " tx-id ":")] (map datom->str datoms)))))))