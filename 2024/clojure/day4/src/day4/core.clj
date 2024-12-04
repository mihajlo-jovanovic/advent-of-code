(ns day4.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-grid-map
  "Parses a multi-line string input into a map with [x y] coordinates as keys and characters as values."
  [input]
  (let [lines (s/split-lines input)]
    (into {}
          (for [y (range (count lines))
                :let [line (nth lines y)]
                x (range (count line))]
            [[x y] (nth line x)]))))

(def directions
  "All eight possible directions from a point."
  [[0 -1] [1 -1] [1 0] [1 1] [0 1] [-1 1] [-1 0] [-1 -1]])

(defn matches-word? [grid-map x y word]
  (let [word-length (count word)]
    (->> directions
         (reduce (fn [count [dx dy]]
                   (loop [i 0]
                     (cond
                       (= i word-length) (inc count)
                       :else (let [pos [(+ x (* i dx)) (+ y (* i dy))]
                                   expected-char (nth word i)
                                   actual-char (grid-map pos)]
                               (if (= expected-char actual-char)
                                 (recur (inc i))
                                 count)))))
                 0))))

(defn matches-word-p2? [grid-map x y]
  (and (= (grid-map [x y]) \A)
       (or (and (= (grid-map [(inc x) (dec y)]) \M) (= (grid-map [(dec x) (inc y)]) \S))
           (and (= (grid-map [(inc x) (dec y)]) \S) (= (grid-map [(dec x) (inc y)]) \M)))
       (or (and (= (grid-map [(inc x) (inc y)]) \M) (= (grid-map [(dec x) (dec y)]) \S))
           (and (= (grid-map [(inc x) (inc y)]) \S) (= (grid-map [(dec x) (dec y)]) \M)))))

(defn p1 [grid]
  (reduce (fn [acc [x y]]
            (+ acc (matches-word? grid x y "XMAS")))
          0
          (keys grid)))

(defn p2 [grid]
  (count (filter identity
                 (map (fn [[x y]]
                        (matches-word-p2? grid x y))
                      (keys grid)))))

(defn -main []
  (let [grid-map (parse-grid-map (slurp "resources/input.txt"))]
    (println "Part 1: " (p1 grid-map))
    (time (println "Part 2: " (p2 grid-map)))))