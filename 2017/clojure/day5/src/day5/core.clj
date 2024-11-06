(ns day5.core
  (:gen-class)
  (:require [clojure.string :as string]))

(defn parse-input [filename]
  (->> (slurp filename)
       string/split-lines
       (map #(Integer/parseInt %))
       vec))

(defn make-state [v]
  {:list v :index 0})

(defn jump-v1 [{:keys [list] :as state}]
  (loop [current-state state
         jumps 0]  ; Set to track visited indices 
    (let [current-index (:index current-state)]
      (if (not (<= 0 current-index (dec (count list))))
        jumps
        (recur (assoc (assoc current-state :index (+ current-index (nth (:list current-state) current-index))) :list (update (:list current-state) current-index inc))
               (inc jumps))))))

(defn jump-v2 [state]
  (loop [current-state state
         jumps 0]
    (let [list (:list current-state)
          index (:index current-state)]
      (if (or (neg? index) (>= index (count list))) ; Exit if index is out of bounds
        jumps
        (let [jump-value (nth list index) ; Value to jump by
              updated-list (update list index inc) ; Increment current index
              new-index (+ index jump-value)] ; Calculate new index
          (recur {:list updated-list :index new-index} ; Update state
                 (inc jumps)))))))

(defn update-state-p1
  "Calculates the next state by updating the list at the current index and moving to the new index."
  [{:keys [list index]}]
  (let [jump-value (nth list index)
        updated-list (update list index inc)
        new-index (+ index jump-value)]
    {:list updated-list, :index new-index}))

(defn update-state-p2
  "Calculates the next state by updating the list at the current index and moving to the new index."
  [{:keys [list index]}]
  (let [jump-value (nth list index)
        updated-list (if (>= jump-value 3) (update list index dec) (update list index inc))
        new-index (+ index jump-value)]
    {:list updated-list, :index new-index}))

(defn jump [state f]
  (->> (iterate f state)
       (take-while #(let [idx (:index %)]
                      (and (>= idx 0) (< idx (count (:list %))))))
       count))

(defn -main []
  (let [filename "resources/day5.txt"
        state (make-state (parse-input filename))]
    (time (println "Part 1:" (jump state update-state-p1)))
    (time (println "Part 2:" (jump state update-state-p2)))))