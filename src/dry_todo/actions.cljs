(ns ^:figwheel-always dry-todo.actions
  (:require [dry-todo.action-components :refer [new-todo-text-input add-todo-button
                                                remove-todo-button todo-complete-checkbox
                                                choose-filter]]
            [dry-todo.datascript-entities :refer [update-entity! add-entity! remove-entity!
                                                  get-all-entities get-todos]]
            [dry-todo.transforms :refer [change-text wipe-out-text toggle-complete]]
            [dommy.core :refer-macros [sel sel1]]))

(defn todo-filter-action [todo-filter]
  {:component         (choose-filter todo-filter)
   :entity-type       :filter
   :event-type        :on-click
   :entity-transform  (fn [filter _]
                        (assoc filter :todo-status todo-filter))
   :possibility-check (fn [filter]
                        (and (not= (:todo-status filter) todo-filter)
                             (> (count (get-todos)) 0)))})


(def todo-actions
  {:fill-in-new-todo         {:component        new-todo-text-input
                              :entity-type      :new-todo
                              :event-type       :on-change
                              :entity-transform change-text}

   :add-new-todo             {:component         add-todo-button
                              :entity-type       :new-todo
                              :event-type        :on-click
                              :entity-transform  wipe-out-text
                              :possibility-check (fn [new-todo]
                                                   (not (empty? (:text new-todo))))
                              :add-entity?       :todo}

   :remove-todo              {:component      remove-todo-button
                              :entity-type    :todo
                              :event-type     :on-click
                              :remove-entity? true}

   :toggle-todo-complete     {:component        todo-complete-checkbox
                              :entity-type      :todo
                              :event-type       :on-click
                              :entity-transform toggle-complete}

   :choose-incomplete-filter (todo-filter-action :incomplete)
   :choose-complete-filter   (todo-filter-action :complete)
   :choose-all-filter        (todo-filter-action :all)})



(defn event-handlers-for-action [action-name entity]
  (let [action (action-name todo-actions)
        {:keys [event-type entity-transform remove-entity?
                add-entity? entity-type possibility-check]} action]
    {event-type   (fn [e]
                    (let [can-take-action (or (nil? possibility-check)
                                              (possibility-check entity))]
                      (when can-take-action
                        (let [event-value (.-value (.-target e))]
                          (when entity-transform
                            (update-entity! (entity-transform entity event-value)))
                          (when remove-entity?
                            (remove-entity! entity))
                          (when add-entity?
                            (add-entity! entity add-entity?)))))
                    (.preventDefault e))
     :data-eid    (:db/id entity)
     :data-action action-name}))

(defn component-for [action-name entity]
  (let [component (:component (action-name todo-actions))]
    (component entity
               (event-handlers-for-action action-name
                                          entity))))


(defn actions-for [entity]
  (filter (fn [[action-name action]]
            (let [{:keys [entity-type possibility-check]} action]
              (and (= (:type entity) entity-type)
                   (or (nil? possibility-check)
                       (possibility-check entity)))))
          todo-actions))


; pick random entity
; look at possible actions for it
; do that action


(defn get-elements-by-data [attr-name attr-value]
  (sel (str "*[data-" attr-name "='" attr-value "']")))


(defn get-element-for-entity [action-name entity]
  (let [by-eid (get-elements-by-data "eid" (:db/id entity))
        by-action (get-elements-by-data "action" (name action-name))]
    (first (clojure.set/intersection (set by-eid)
                                     (set by-action)))))

(defn fire!
  "Creates an event of type `event-type`, optionally having
   `update-event!` mutate and return an updated event object,
   and fires it on `node`.
   Only works when `node` is in the DOM"
  [node event-type & [update-event!]]
  (let [update-event! (or update-event! identity)]
    (if (.-createEvent js/document)
      (let [event (.createEvent js/document "Event")]
        (.initEvent event (name event-type) true true)
        (.dispatchEvent node (update-event! event)))
      (.fireEvent node (str "on" (name event-type))
                  (update-event! (.createEventObject js/document))))))

(defn random-string []
  (reduce str (take (+ 5 (rand-int 7)) (repeatedly #(rand-nth (map char (range 33 127)))))))


(defn perform-action [entity action-name action-to-perform]
  (let [element (get-element-for-entity action-name entity)]
    (case (:event-type action-to-perform)
      :on-change (case (dommy.core/attr element :type)
                   "text" (do
                            (dommy.core/set-value! element (random-string))
                            (fire! element :input))
                   "checkbox" (do
                                (set! (.-checked element) (not (.-checked element)))
                                (fire! element :input)
                                (fire! element :selectionchange)
                                (fire! element :change)))
      :on-click (fire! element :click)
      nil)))

(defn fuck []
  (let [entity-to-act-on (rand-nth (get-all-entities))
        possible-actions (actions-for entity-to-act-on)]
    (when (not (empty? possible-actions))
      (let [[action-name action-to-perform] (rand-nth possible-actions)]
        (println action-name)
        (perform-action entity-to-act-on action-name action-to-perform)))))

