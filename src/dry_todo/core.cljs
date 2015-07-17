(ns ^:figwheel-always dry-todo.core
  (:require
    [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)


;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defonce unique-id-counter (atom 0))

(defn get-unique-id! []
  (swap! unique-id-counter inc))

(defonce entities
         (atom [{:id       0
                 :type     :new-todo
                 :text     ""
                 :complete false}]))


(defn add-entity! [new-entity]
  (swap! entities conj (assoc new-entity :id (get-unique-id!))))

(defn create-new-todo [text]
  {:text     text
   :complete false
   :type     :todo})

(defn add-todo-button [_ event-handlers]
  [:button event-handlers
   "Add Todo"])

(defn new-todo-text-input [new-todo event-handlers]
  [:input (merge {:type  "text"
                  :value [(:text new-todo)]}
                 event-handlers)])

(def actions
  {:fill-in-new-todo {:component  new-todo-text-input
                      :event-type :on-change
                      :transform  (fn [new-todo new-todo-text]
                                    (assoc new-todo :text new-todo-text))}
   :add-new-todo     {:component   add-todo-button
                      :event-type  :on-click
                      :global-transforms (fn [new-todo]
                                     (add-entity! (create-new-todo (:text new-todo))))
                      :transform   (fn [new-todo _]
                                     (assoc new-todo :text ""))}})

(defn update-entity! [entity-to-update transform event-value]
  (reset! entities
          (map (fn [entity]
                 (if (= (:id entity-to-update) (:id entity))
                   (transform entity event-value)
                   entity))
               @entities)))


(defn get-event-handlers [action-name entity]
  (let [{:keys [event-type transform global-transforms]} (action-name actions)]
    {event-type (fn [e]
                  (let [event-value (.-value (.-target e))]
                    (update-entity! entity transform event-value)
                    (when global-transforms
                      (global-transforms entity)))
                  (.preventDefault e))}))



(defn component-for-action [action-name entity]
  (let [action (action-name actions)]
    ((:component action) entity (get-event-handlers action-name entity))))

(defn new-todo-form [new-todo]
  [:form
   (component-for-action :fill-in-new-todo new-todo)
   (component-for-action :add-new-todo new-todo)])



(defn get-todos []
  (filter #(= (:type %) :todo) @entities))

(defn get-new-todo []
  (first (filter #(= (:type %) :new-todo) @entities)))

(defn todo-list []
  [:ul
   (for [todo (get-todos)]
     [:li {:key (:id todo)} (:text todo)])])

(defn todo-app []
  [:main
   [new-todo-form (get-new-todo)]
   [todo-list]])

(reagent/render-component [todo-app]
                          (. js/document (getElementById "app")))

