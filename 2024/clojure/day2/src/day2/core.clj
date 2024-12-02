(ns day2.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-line [line]
  (let [parts (s/split line #"\s+")]
    (mapv #(Integer/parseInt %) parts)))

(defn parse-input [filename]
  (map parse-line (s/split-lines (slurp filename))))

(defn valid-differences? [coll]
  (every? #(< 0 % 4) (map (fn [[f s]] (Math/abs (- f s))) (partition 2 1 coll))))

(defn safe? [coll]
  (let [sorted (sort coll)]
    (and (or (= sorted coll) (= sorted (reverse coll)))
         (valid-differences? coll))))

(defn vec-remove [pos coll]
  (concat (take pos coll) (drop (inc pos) coll)))

(defn drop-1-all-combinations [coll]
  (map #(vec-remove % coll) (range (count coll))))

(defn p1 [input]
  (count (filter safe? input)))

(defn safe-p2? [coll]
  (some safe? (drop-1-all-combinations coll)))

(defn p2 [input]
  (count (filter safe-p2? input)))

(defn -main []
  (let  [input (parse-input "resources/input.txt")]
    (time (println "Part 1" (p1 input)))
    (time (println "Part 2" (p2 input)))))