(ns day11.core
  (:require [clojure.string :as str]
            [day11.topo :as topo])
  (:gen-class))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))
        parsed (map #(re-seq #"\w+" %) lines)]
    parsed))

(defn successors [adj-list v]
  (rest (first (filter #(= v (first %)) adj-list))))

(defn to-edges [adj-list]
  (mapcat #(for [to (rest %)] (vector (first %) to))  adj-list))

(defn get-idx [n topo-sorted]
  (ffirst (filter (fn [[idx nd]] (= nd n)) (map-indexed vector topo-sorted))))

(defn dfs [adj-list f start end topo-sorted]
  (loop [stack (vector start)
         result 0]
    (if (empty? stack)
      result
      (let [curr (peek stack)
            successors (f adj-list curr)]
        (if (= curr end)
          (recur (into (pop stack) successors) (inc result))
          (recur (into (pop stack) successors) result))))))

(def m-count-paths
  (memoize
   (fn count-paths [adj-list f start end topo-sorted]
     (if (or (> (get-idx start topo-sorted) (get-idx end topo-sorted)) (= start end))
       0
       (let [outgoing (f adj-list start)]
         (if (contains? (set outgoing) end)
           (+ 1 (reduce + (map #(m-count-paths adj-list f % end topo-sorted)  outgoing)))
           (reduce + (map #(m-count-paths adj-list f % end topo-sorted) outgoing))))))))

(defn p2 [filepath]
  (let [adj-list (parse-input filepath)
        topo-sorted (topo/topo-sort (to-edges adj-list))]
    (*
     (m-count-paths adj-list successors "svr" "fft" topo-sorted)
     (m-count-paths adj-list successors "fft" "dac" topo-sorted)
     (m-count-paths adj-list successors "dac" "out" topo-sorted))))

(defn -main []
  (println "Part 2: " (time (p2 "resources/day11.txt"))))
