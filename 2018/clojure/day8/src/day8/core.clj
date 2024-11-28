(ns day8.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-input [filename]
  (mapv Integer/parseInt (str/split (str/trim-newline (slurp filename)) #" ")))

(defn parse-tree
  "Parses input string into a tree structure with metadata and children and keys"
  [nums]
  (let [num-of-children (first nums)
        num-of-metadata-entries (second nums)
        remaining-nums (drop 2 nums)]
    (if (zero? num-of-children)
      [{:metadata (into [] (take num-of-metadata-entries remaining-nums))} (drop num-of-metadata-entries remaining-nums)]
      (loop [num-of-children num-of-children
             children []
             remaining-nums remaining-nums]
        (if (zero? num-of-children)
          [{:metadata (into [] (take num-of-metadata-entries remaining-nums)) :children children} (drop num-of-metadata-entries remaining-nums)]
          (let [[child remaining-nums] (parse-tree remaining-nums)]
            (recur (dec num-of-children) (conj children child) remaining-nums)))))))

(defn branch? [node]
  (:children node))

(defn children [node]
  (:children node))

(defn part1 [filename]
  (let [nums (parse-input filename)
        tree (first (parse-tree nums))]
    (reduce + (flatten (map :metadata (tree-seq branch? children tree))))))

(defn part2 [{:keys [metadata children] :as root}]
  (if (branch? root)
    (reduce + (map #(let [i (dec %)] (if (< i (count children)) (part2 (nth children i)) 0)) metadata))
    (reduce + metadata)))