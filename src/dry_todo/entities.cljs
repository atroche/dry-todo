(ns dry-todo.entities
  (:require [reagent.core :as reagent]))

(defonce unique-id-counter (reagent/atom 0))

(defn get-unique-id! []
  (swap! unique-id-counter inc))

(defn new-entity [entity-type data]
  (merge {:id   (get-unique-id!)
          :type entity-type}
         data))

(defn initial-entities []
  (reagent/atom [(new-entity :new-todo {:text "" :complete false})]))

(defonce entities (initial-entities))

(defn add-entity! [new-entity]
  (swap! entities conj (assoc new-entity :id (get-unique-id!))))

(defn update-entity! [entity-to-update transform event-value]
  (swap! entities
         (partial map
                  (fn [entity]
                    (if (= entity-to-update entity)
                      (transform entity event-value)
                      entity)))))

(defn entities-with-type [entities entity-type]
  (filter #(= (:type %) entity-type) entities))




