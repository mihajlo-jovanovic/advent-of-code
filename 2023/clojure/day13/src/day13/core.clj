(ns day13.core
  (:gen-class)
  (:require [clojure.string :as string]))

(defn parse-pattern [pattern]
  (let [parse-line (fn [y line]  (->> line
                                      (map-indexed vector)
                                      (filter #(= \. (second %)))
                                      (map first)
                                      (map #(vector % y))))]
    (->> pattern
         (string/split-lines)
         (map-indexed parse-line)
         (apply concat)
         set)))

(defn parse-input [input]
  (->> (string/split input #"\n\n")
       (map parse-pattern)))

(defn get-y [pattern x]
  (->> pattern
       (filter #(= x (first %)))
       (map second)))

(defn get-x [pattern y]
  (->> pattern
       (filter #(= y (second %)))
       (map first)))

(defn equal-non-empty-lists?
  [[l r]]
  (if (or (empty? l) (empty? r))
    false
    (= l r)))

(defn reflection-point
  "Returns the reflection point of a pattern"
  [vertical? pattern n1 n2]
  (let [f (if vertical? first second)
        max-n (apply max (map f pattern))
        get-n (if vertical? get-y get-x)]
    (if (or (< n1 0) (> n2 max-n)) true
        (let [v1 (sort (get-n pattern n1))
              v2 (sort (get-n pattern n2))]
          (if (not (equal-non-empty-lists? (vector v1 v2)))
            false
            (reflection-point vertical? pattern (dec n1) (inc n2)))))))

(def reflection-point-vertical? (partial reflection-point true))
(def reflection-point-horizontal? (partial reflection-point false))

(defn part1
  [pattern]
  (let [max-x (apply max (map first pattern))
        max-y (apply max (map second pattern))
        v (filter #(reflection-point-vertical? pattern (first %) (second %)) (partition 2 1 (range (inc max-x))))]
    (if (empty? v)
      (* 100 (inc (first (first (filter #(reflection-point-horizontal? pattern (first %) (second %)) (partition 2 1 (range (inc max-y))))))))
      (inc (first (first v))))))

(defn get-reflection-points-as-vector
  [pattern]
  (let [max-x (apply max (map first pattern))
        max-y (apply max (map second pattern))
        v (filter #(reflection-point-vertical? pattern (first %) (second %)) (partition 2 1 (range (inc max-x))))
        h (filter #(reflection-point-horizontal? pattern (first %) (second %)) (partition 2 1 (range (inc max-y))))]
    (if (and (empty? v) (empty? h))
      []
      (if (empty? v)
        (into [] (map #(vector 0 (inc (first %))) h))
        (if (empty? h)
          (into [] (map #(vector (inc (first %)) 0) v))
          (into [] (concat (map #(vector (inc (first %)) 0) v) (into [] (map #(vector 0 (inc (first %))) h)))))))))

(defn score
  [[vertical horizontal]]
  (+
   (* 100 horizontal)
   vertical))

(defn part2
  [pattern]
  (let [old-reflection (get-reflection-points-as-vector pattern)
        max-x (inc (apply max (map first pattern)))
        max-y (inc (apply max (map second pattern)))
        new-reflection (first (for [x (range max-x)
                                    y (range max-y)
                                    :let [pattern-new (if (contains? pattern [x y])
                                                        (disj pattern [x y])
                                                        (conj pattern [x y]))
                                          reflection-points (get-reflection-points-as-vector pattern-new)]
                                    :when (not (or (empty? reflection-points) (= reflection-points old-reflection)))]
                                reflection-points))]
    (score (first (filter #(not= % (first old-reflection)) new-reflection)))))

(defn -main
  []
  (let [input (slurp "resources/day13.txt")]
    (println "Part 1: " (reduce + (map part1 (parse-input input))))
    (println "Part 2: " (reduce + (map part2 (parse-input input))))))