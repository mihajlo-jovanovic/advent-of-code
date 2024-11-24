(ns day22.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-input
  [input]
  (let [lines (s/split-lines input)
        size  (count lines)
        half  (quot size 2)] ;; Calculate the middle of the grid
    (->> lines
         (map-indexed (fn [y line]
                        (map-indexed (fn [x char]
                                       (when (= char \#)
                                         [(- x half) (- half y)]))
                                     line)))
         (apply concat)
         (remove nil?)
         set)))

(defn turn-right [direction]
  (case direction
    :up :right
    :right :down
    :down :left
    :left :up))

(defn turn-left [direction]
  (case direction
    :up :left
    :left :down
    :down :right
    :right :up))

(defn turn-back [direction]
  (case direction
    :up :down
    :down :up
    :left :right
    :right :left))

(defn simulate-burst [{:keys [current direction infections counter]}]
  (let [new-direction (if (contains? infections current)
                        (turn-right direction)
                        (turn-left direction))
        new-infections (if (infections current)
                         (disj infections current)
                         (conj infections current))
        new-current (case new-direction
                      :up [(first current) (inc (second current))]
                      :down [(first current) (dec (second current))]
                      :left [(dec (first current)) (second current)]
                      :right [(inc (first current)) (second current)])
        new-counter (if (infections current)
                      counter
                      (inc counter))
        new-state {:current new-current :direction new-direction :infections new-infections :counter new-counter}]
    new-state))

(defn simulate-burst-p2 [{:keys [current direction infections weakened flagged counter]}]
  (let [new-direction (cond
                        (contains? infections current)
                        (turn-right direction)
                        (contains? weakened current)
                        direction
                        (contains? flagged current)
                        (turn-back direction)
                        :else (turn-left direction))
        new-infections (cond
                         (infections current) (disj infections current)
                         (weakened current) (conj infections current)
                         :else infections)
        new-weakened (cond
                       (weakened current) (disj weakened current)
                       (and (not (infections current)) (not (flagged current))) (conj weakened current)
                       :else weakened)
        new-flagged (cond
                      (flagged current) (disj flagged current)
                      (infections current) (conj flagged current)
                      :else flagged)
        new-current (case new-direction
                      :up [(first current) (inc (second current))]
                      :down [(first current) (dec (second current))]
                      :left [(dec (first current)) (second current)]
                      :right [(inc (first current)) (second current)])
        new-counter (if (< (count infections) (count new-infections))
                      (inc counter)
                      counter)
        new-state {:current new-current :direction new-direction :infections new-infections :weakened new-weakened :flagged new-flagged :counter new-counter}]
    new-state))

(defn -main []
  (let [infections (parse-input (slurp "resources/input.txt"))
        state {:current [0 0] :direction :up :infections infections :weakened #{} :flagged #{} :counter 0}]
    (println "Part 1: " (:counter (last (take 10001
                                              (iterate simulate-burst state)))))
    (time (println "Part 2: " (:counter (last (take 10000001
                                                    (iterate simulate-burst-p2 state))))))))