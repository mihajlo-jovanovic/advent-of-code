(ns day25.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-grid
  [input]
  (let [parts (s/split input #"\n\n")]
    (map
     #(let [lines (s/split-lines %)]
        (for [y (range (count lines))
              x (range (count (nth lines y)))
              :when (not= \. (nth (nth lines y) x))]
          [x y])) parts)))

(defn convert-lock-to-pin-heights [lock]
  (map #(apply max (map (fn [[_ y]] y) (second %))) (group-by first lock)))

(defn convert-key-to-pin-heights [key]
  (map #(- 6 (apply min (map (fn [[_ y]] y) (second %)))) (sort (group-by first key))))

(defn lock? [coll]
  (let [s (set coll)]
    (every? #(contains? s [% 0]) (range 5))))

(defn fit-togeter? [key lock]
  (let [key-pins (convert-key-to-pin-heights key)
        lock-pins (convert-lock-to-pin-heights lock)]
    (every? (fn [n] (<= n 5)) (map #(reduce + %) (map vector lock-pins key-pins)))))

(defn part1 [input]
  (let [grp (group-by lock? input)
        lock (get grp true)
        keys (get grp false)]
    (reduce + (for [l lock
                    k keys
                    :when (fit-togeter? k l)]
                1))))

(defn -main []
  (let [filename "resources/input.txt"
        input (parse-grid (slurp filename))]
    (println "Part 1: " (part1 input))))