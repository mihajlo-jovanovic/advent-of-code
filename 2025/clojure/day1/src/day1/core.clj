(ns day1.core
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (let [lines (slurp filename)
        nums (map #(Long/parseLong %) (s/split-lines lines))]
    (vec nums)))

(defn times-cross-zero [n1 n2]
  (let [r (if (> n1 n2) (range n2 n1) (range (inc n1) (inc n2)))]
    (count (filter #(= 0 (mod % 100)) r))))

(defn p1 [filename]
  (count (filter #(= 0 (mod % 100)) (reductions + 50 (parse-input filename)))))

(defn p2 [filename]
  (let [nums (parse-input filename)]
    (reduce (fn [[counter n2] n1]
              (let [times (times-cross-zero n2 (+ n1 n2))]
                [(+ times counter) (+ n1 n2)]))
            [0 50] nums)))
