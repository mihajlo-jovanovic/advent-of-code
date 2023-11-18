(ns day2.core
  (:gen-class)
  (:require [clojure.string :as string]))

(defn parse-input
  "Parses the input file into a collection of collections of integers"
  [f]
  (->> f
       slurp
       (string/split-lines)
       (map #(string/split % #"\t"))
       (map (fn [subseq] (map #(Integer/parseInt %) subseq)))))

(defn evenly-div
  "Returns the result of dividing the first evenly divisible pair in the collection"
  [coll]
  (first
   (for [x coll
         y coll
         :when (and (not= x y) (zero? (mod x y)))]
     (/ x y))))

(defn diff
  "Returns the difference between the largest and smallest number in the collection"
  [coll]
  (let [mx (apply max coll)
        mn (apply min coll)]
    (- mx mn)))

(defn solve
  [f coll]
  (reduce + (map f coll)))

(defn -main
  [& _]
  (let [input (parse-input "resources/day2.txt")]
    (println "Part 1: " (solve diff input))
    (println "Part 2: " (solve evenly-div input))))