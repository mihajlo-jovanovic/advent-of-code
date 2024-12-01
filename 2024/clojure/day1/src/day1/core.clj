(ns day1.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (let [lines (s/split-lines (slurp filename))]
    (map (fn [line] (map #(Integer/parseInt %) (s/split line #"\s+"))) lines)))

(defn count-occurrences [s slist]
  (->> slist
       (filter #{s})
       count))

(defn -main []
  (let [input (parse-input "resources/input.txt")
        l1 (mapv first input)
        l2 (mapv second input)
        pairs (map vector (sort l1) (sort l2))]
    (println "Part 1: " (reduce + (map #(Math/abs (- (first %) (second %))) pairs)))
    (println "Part 2: " (reduce + (map #(* (first %) (count-occurrences (first %) l2)) pairs)))))