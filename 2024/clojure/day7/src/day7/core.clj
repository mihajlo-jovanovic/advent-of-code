(ns day7.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-line [line]
  (let [parts (s/split line #": ")
        result (Long/parseLong (first parts))
        nums (map #(Long/parseLong %) (s/split (second parts) #" "))]
    [result nums]))

(defn parse-input [filename]
  (let [lines (s/split-lines (slurp filename))]
    (map parse-line lines)))

(defn all-possibilities [colls]
  (reduce
   (fn [acc n] (concat (map #(* n %) acc) (map #(+ n %) acc)))
   #{(first colls)}
   (drop 1 colls)))

(defn valid? [[k v]]
  (contains? (set (all-possibilities v)) k))

(defn part1 [input]
  (reduce + (map first (filter valid? input))))

(defn concatination-op [n1 n2]
  (Long/parseLong (apply str (list n1 n2))))

;; just copy and paste for now; wil refactor later
(defn all-possibilities-p2 [colls]
  (reduce
   (fn [acc n] (concat (map #(concatination-op % n) acc) (map #(* n %) acc) (map #(+ n %) acc)))
   #{(first colls)}
   (drop 1 colls)))

(defn valid-p2? [[k v]]
  (contains? (set (all-possibilities-p2 v)) k))

(defn part2 [input]
  (reduce + (map first (filter valid-p2? input))))