(ns ^:figwheel-always dry-todo.core
  (:require [dry-todo.datascript-entities :refer [conn]]
            [dry-todo.components :refer [todo-app]]
            [rum]
            [datascript :as d]
            [clojure.string :as str]))

 (enable-console-print!)

(defn mount-app [db]
  (rum/mount (todo-app db)
             (. js/document (getElementById "app"))))


(mount-app @conn)

(d/listen! conn
           :render
           (fn [tx-report]
             (mount-app (:db-after tx-report))))


(defn on-js-reload [])



(d/listen! conn :log
           (fn [tx-report]
             (let [tx-id  (get-in tx-report [:tempids :db/current-tx])
                   datoms (:tx-data tx-report)
                   datom->str (fn [d] (str (if (.-added d) "+" "−")
                                           "[" (.-e d) " " (.-a d) " " (pr-str (.-v d)) "]"))]
               (println
                 (str/join "\n" (concat [(str "tx " tx-id ":")] (map datom->str datoms)))))))