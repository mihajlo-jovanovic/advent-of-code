(ns day8.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.math.combinatorics :as combo]))

(defn parse-grid
  "Parse a multi-line string into a map with {:anthenas :size} keys, where anthenas is a map of values to list of coordinates."
  [filename]
  (let [lines (s/split-lines (slurp filename))]
    {:anthenas (->> (for [y (range (count lines))
                          x (range (count (nth lines y)))
                          :let [c (nth (nth lines y) x)]
                          :when (not= c \.)]
                      {c [x y]})
                    (reduce (fn [acc m]
                              (let [[k v] (first m)]
                                (update acc k (fnil conj #{}) v)))
                            {})) :size (count lines)}))

(defn extend-diagonal
  [[[x1 y1] [x2 y2]]]
  (let [dx (- x2 x1)
        dy (- y2 y1)]
    [[(- x1 dx) (- y1 dy)]
     [(+ x2 dx) (+ y2 dy)]]))

(defn off-grid? [[x y] grid-size]
  (or (neg? x)
      (neg? y)
      (>= x grid-size)
      (>= y grid-size)))

(defn part1 [filename]
  (let [{:keys [anthenas size]} (parse-grid filename)]
    (loop [groups anthenas
           acc #{}]
      (if (empty? groups)
        (count acc)
        (let [[_ v] (first groups)
              antinodes-new (filter #(not (off-grid? % size)) (apply concat (map extend-diagonal (combo/combinations v 2))))]
          (recur (rest groups) (into acc antinodes-new)))))))

(defn line-points-in-range
  [[[x1 y1] [x2 y2]] grid-size]
  (let [dx (- x2 x1)
        dy (- y2 y1)]

    ;; A helper function to generate points in one direction until out of range
    (defn generate-points [start-x start-y step-x step-y]
      (->> (iterate (fn [[x y]] [(+ x step-x) (+ y step-y)]) [start-x start-y])
           (rest) ;; skip the starting point itself here, we will add it separately
           (take-while (fn [[x y]] (not (off-grid? [x y] grid-size))))))

    ;; Generate backward points (in the opposite direction)
    (let [backward-points (generate-points x1 y1 (- dx) (- dy))
          forward-points  (generate-points x1 y1 dx dy)]

      ;; Combine all points: backward, the initial point, and forward
      (concat backward-points [[x1 y1]] forward-points))))

(defn part2 [filename]
  (let [{:keys [anthenas size]} (parse-grid filename)]
    (loop [groups anthenas
           antinodes #{}]
      (if (empty? groups)
        (count antinodes)
        (let [[_ v] (first groups)
              all-points (vec (map vec (combo/combinations v 2)))
              antinodes-new (apply concat (map #(line-points-in-range % size) all-points))]
          (recur (rest groups) (into antinodes antinodes-new)))))))

(defn -main
  []
  (time (println (part1 "resources/input.txt")))
  (time (println (part2 "resources/input.txt"))))