(ns day10.core
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as s]))

(defn parse-grid
  [filename]
  (let [lines (s/split-lines (slurp filename))]
    {:grid (->> (for [y (range (count lines))
                      x (range (count (nth lines y)))
                      :let [c (nth (nth lines y) x)]
                      :when (not= c \.)]
                  {[x y] (Integer/parseInt (str c))})
                (apply merge)) :size (count lines)}))

(defn neighbors
  "Returns neighbors that are directly up, down, left, or right on a 3D grid; no diagonal neighbors."
  [[x y]]
  [[x (- y 1)]
   [x (+ y 1)]
   [(- x 1) y]
   [(+ x 1) y]])

(defn off-grid? [max-sz [x y]]
  (or (neg? x)
      (neg? y)
      (>= x max-sz)
      (>= y max-sz)))

(defn children [{:keys [grid size]} [x y]]
  (let [current (get grid [x y])]
    (filter #(= (inc current) (get grid %)) (filter (complement (partial off-grid? size)) (neighbors [x y])))))

(defn part1 [input]
  (let [start (map first (filter #(= 0 (val %)) (:grid input)))
        goals (into #{} (map first (filter #(= 9 (val %)) (:grid input))))]
    (reduce + (map (fn [root]
                     (count
                      (set/intersection
                       goals
                       (into #{} (tree-seq (complement empty?) (partial children input) root)))))
                   start))))

(defn -main []
  (time (println "Part 1: " (part1 (parse-grid "resources/input.txt")))))