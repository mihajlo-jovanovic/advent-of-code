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
  [[x (dec y)]
   [x (inc y)]
   [(dec x) y]
   [(inc x) y]])

(defn off-grid? [max-sz [x y]]
  (or (neg? x)
      (neg? y)
      (>= x max-sz)
      (>= y max-sz)))

(defn children [{:keys [grid size]} [x y]]
  (let [current (get grid [x y])]
    (->> (neighbors [x y])
         (remove (partial off-grid? size))
         (filter (fn [n] (= (inc current) (grid n)))))))

(defn part1 [input]
  (let [grid (:grid input)
        start (for [[coord val] grid :when (= val 0)] coord)
        goals (into #{} (map first (filter #(= 9 (val %)) grid)))]
    (->> start
         (map (fn [root]
                (->> root
                     (tree-seq seq (partial children input))
                     (into #{})
                     (set/intersection goals)
                     (count))))
         (reduce +))))

(defn part2 [input]
  (let [grid (:grid input)
        count-distinct-paths (fn [input start goal] (count (filter #(= % goal) (tree-seq seq (partial children input) start))))]
    (reduce + (for [start (for [[coord val] grid :when (= val 0)] coord)
                    goals (map first (filter #(= 9 (val %)) grid))]
                (count-distinct-paths input start goals)))))

(defn -main []
  (time (println "Part 1: " (part1 (parse-grid "resources/input.txt"))))
  (time (println "Part 2: " (part2 (parse-grid "resources/input.txt")))))