(ns day11.core
  (:gen-class)
  (:require [clojure.math.combinatorics :as combo]
            [clojure.set :as set]
            [clojure.string :as string]))

(defn parse-input [input]
  (let [process-line (fn [y line] (->> line
                                       (map-indexed vector)
                                       (filter #(not (= \. (second %))))
                                       (map #(vector (first %) y))))]
    (->> input
         (string/split-lines)
         (map-indexed process-line)
         (filter #(seq %))
         (apply concat))))

(defn manhattan-distance [[x1 y1] [x2 y2] empty-rows empty-cols expand-factor]
  (let [x-diff (Math/abs (- x1 x2))
        y-diff (Math/abs (- y1 y2))
        x-empty (count (filter #(if (< x1 x2) (< x1 % x2) (< x2 % x1)) empty-cols))
        y-empty (count (filter #(if (< y1 y2) (< y1 % y2) (< y2 % y1)) empty-rows))
        x (if (pos? x-empty) (+ (* expand-factor x-empty) (- x-diff x-empty)) x-diff)
        y (if (pos? y-empty) (+ (* expand-factor y-empty) (- y-diff y-empty)) y-diff)]
    (+ x y)))

(defn solve
  [input expand-factor]
  (let [sz (apply max (map first input))
        empty-rows (into [] (set/difference (into #{} (range (inc sz))) (into #{} (map second input))))
        empty-cols (into [] (set/difference (into #{} (range (inc sz))) (into #{} (map first input))))]
    (reduce + (map #(manhattan-distance (first %) (second %) empty-rows empty-cols expand-factor) (combo/combinations input 2)))))

(defn -main
  []
  (let [input (parse-input (slurp "resources/day11.txt"))]
    (println "Part 1" (solve input 2))
    (println "Part 2" (solve input 1000000))))