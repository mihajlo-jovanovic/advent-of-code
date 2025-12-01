(ns day1.core
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (->> (slurp filename)
       (s/split-lines)
       (map #(Long/parseLong %))
       (vec)))

(defn times-cross-zero [n1 n2]
  (let [divisible-by-100? #(= 0 (mod % 100))
        dial-clicks (if (> n1 n2) (range n2 n1) (range n2 n1 -1))]
    (count (filter divisible-by-100? dial-clicks))))

(defn p1 [filename]
  (->> (parse-input filename)
       (reductions + 50)
       (filter #(= 0 (mod % 100)))
       (count)))

(defn p2 [filename]
  (let [nums (parse-input filename)]
    (reduce (fn [[counter n2] n1]
              (let [times (times-cross-zero n2 (+ n1 n2))]
                [(+ times counter) (+ n1 n2)]))
            [0 50] nums)))
