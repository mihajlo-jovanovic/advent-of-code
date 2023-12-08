(ns day7.core
  (:require [clojure.string :as str])
  (:gen-class))

(def card-order-p1 "23456789TJQKA")
(def card-order-p2 "J23456789TQKA")

(defn card-strength [card-order card]
  (.indexOf card-order (str card)))

(defn hand-strength [hand card-order]
  (into [] (map (partial card-strength card-order) hand)))

(defn sorted-freq-counts [hand]
  (->> hand
       frequencies
       vals
       sort
       reverse
       vec))

(defn compare-by-type-p1 [hand1 hand2]
  (let [h1 (sorted-freq-counts hand1)
        h2 (sorted-freq-counts hand2)
        min-length (min (count h1) (count h2))]
    (compare (subvec h1 0 min-length) (subvec h2 0 min-length))))

(defn process-hand [hand]
  (let [cnt-j (count (filter #(= % \J) hand))
        without-j (apply str (remove #(= \J %) hand))]
    [cnt-j (sorted-freq-counts without-j)]))

(defn compare-by-type-p2 [hand1 hand2]
  (let [[cnt-j1 h1] (process-hand hand1)
        [cnt-j2 h2] (process-hand hand2)
        min-length (min (count h1) (count h2))
        sub-h1 (subvec h1 0 min-length)
        sub-h2 (subvec h2 0 min-length)]
    (compare (assoc sub-h1 0 (+ cnt-j1 (or (first h1) 0)))
             (assoc sub-h2 0 (+ cnt-j2 (or (first h2) 0))))))

(defn compare-hands-p1 [hand1 hand2]
  (let [c (compare-by-type-p1 hand1 hand2)]
    (if (zero? c)
      (compare (hand-strength hand1 card-order-p1) (hand-strength hand2 card-order-p1))
      c)))

(defn compare-hands-p2 [hand1 hand2]
  (let [c (compare-by-type-p2 hand1 hand2)]
    (if (zero? c)
      (compare (hand-strength hand1 card-order-p2) (hand-strength hand2 card-order-p2))
      c)))

(defn parse-input [input]
  (map (comp #(zipmap [:hand :bid] %) #(str/split % #"\s"))
       (str/split-lines input)))

(defn solve [hands compare-by]
  (reduce +
          (map
           #(* (inc (first %)) (Integer/parseInt (:bid (second %))))
           (map-indexed vector (sort-by :hand compare-by hands)))))

(defn p1 [input]
  (solve input compare-hands-p1))

(defn p2 [input]
  (solve input compare-hands-p2))

(defn -main [& _]
  (let [input (parse-input (slurp "resources/day7.txt"))]
    (println "Part 1:" (p1 input))
    (println "Part 2:" (p2 input))))