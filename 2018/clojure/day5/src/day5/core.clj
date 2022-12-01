(ns day5.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str])
  (:gen-class))

(defn polar-opposite
  [a b] (and (= (str/upper-case a) (str/upper-case b)) (not= a b)))

(defn react [a b]
  (if-let [a1 (last (str a))]
    (if (polar-opposite a1 b)
      (str/join "" (drop-last (str a)))
      (str a b))
    b))

(defn p1 [s]
  (count (reduce react s)))

(defn first-duplicate [coll]
  (reduce (fn [acc x]
            (if (contains? acc x)
              (reduced x)
              (conj acc x)))
          #{} coll))

;; alternate implementation using recursion
(defn p1-alt [s]
  (let [helper (fn [s acc]
                 (if (= 1 (count s))
                   (conj acc (first s))
                   (let [a (first s)
                         b (rest s)]
                     (if (polar-opposite a (first b))
                       (recur (rest b) acc)
                       (recur b (conj acc a))))))]
    (helper s [])))

(defn p1-alternate [s]
  (first-duplicate (map count (take 10000 (iterate p1-alt s)))))

(defn -main
  [& _]
  (let [input (-> "day5.txt"
                  io/resource
                  str
                  slurp
                  str/trim-newline)]
    (println "Part 1 solution: " (time (p1 input)))
    (println "Part 1 solution (alternate): " (time (p1-alternate input)))))