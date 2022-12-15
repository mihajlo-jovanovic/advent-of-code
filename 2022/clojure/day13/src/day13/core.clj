(ns day13.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(defn compare-helper
  "Returns true if a number is equal to a list, accounting for nesting rules"
  [n l]
  (if (or (empty? l) (> (count l) 1))
    false
    (let [f (first l)]
      (if (number? f)
        (= n f)
        (compare-helper n f)))))

(defn compare-lists [coll1 coll2]
  (cond
    (empty? coll1) true
    (empty? coll2) false
    :else (let [l (first coll1)
                r (first coll2)]
            (cond
              (and (number? l) (number? r)) (case (compare l r)
                                              -1 true
                                              1 false
                                              0 (compare-lists (rest coll1) (rest coll2)))
              (number? l) (if (compare-lists (vector l) r)
                            ;; need to check for a case when elements are equal
                            ;; and therefore we need to continue lookig
                            (if (compare-helper l r)
                              (compare-lists (rest coll1) (rest coll2))
                              true)
                            false)
              (number? r) (if (compare-lists l (vector r))
                            (if (compare-helper r l)
                              (compare-lists (rest coll1) (rest coll2))
                              true)
                            false)
              :else (if (compare-lists l r)
                      (if (= (flatten l) (flatten r))
                        (compare-lists (rest coll1) (rest coll2))
                        true)
                      false)))))

(defn -main [& _]
  (let [input (-> "sample.txt"
                  io/resource
                  slurp)
        tokens (str/split input #"\n\n")
        tokens2 (map read-string (str/split-lines (str/replace input "\n\n" "\n")))
        div1 [[2]]
        div2 [[6]]
        idx (fn [n coll] (reduce + (map #(if % 1 0) (map #(compare-lists % n) coll))))]
    (println "Part 1 solution: " (->> tokens
                                      (map str/split-lines)
                                      (map #(map read-string %))
                                      (map #(compare-lists (first %) (second %)))
                                      (map-indexed vector)
                                      (map #(if (second %) (inc (first %)) 0))
                                      (reduce +)))
    (println "Part 2 solution: " (* (inc (idx div1 tokens2)) (+ (idx div2 tokens2) 2)))))