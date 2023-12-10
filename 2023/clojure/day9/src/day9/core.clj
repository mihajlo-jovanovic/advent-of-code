(ns day9.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-input [s]
  (map
   (fn [line]
     (vec (map #(Integer/parseInt %) (str/split line #"\s+"))))
   (str/split-lines s)))

(defn next-number [input]
  (if (every? #(zero? %) input)
    0
    (+ (last input) (next-number (map #(- (second %) (first %)) (partition 2 1 input))))))

(defn next-number-p2 [input]
  (if (every? #(zero? %) input)
    0
    (- (first input) (next-number-p2 (map #(- (second %) (first %)) (partition 2 1 input))))))

(defn -main
  []
  (let [input (parse-input (slurp "resources/day9.txt"))]
    (println "Part 1: " (reduce + (map next-number input)))
    (println "Part 2: " (reduce + (map next-number-p2 input)))))
