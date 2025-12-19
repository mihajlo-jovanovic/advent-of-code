(ns day8.core
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [clojure.set :as s])
  (:gen-class))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))
        parse-coords (fn [s] (map Long/parseLong (str/split s #",")))]
    (map parse-coords lines)))

(defn distance [[x1 y1 z1] [x2 y2 z2]]
  math/sqrt (+ (math/pow (- x1 x2) 2) (math/pow (- y1 y2) 2) (math/pow (- z1 z2) 2)))

(defn edges-seq [nodes]
  (map second (sort-by first (for [x1 nodes x2 nodes] (if (= x1 x2) [Long/MAX_VALUE [x1 x2]] [(distance x1 x2) [x1 x2]])))))

(defn successors [edges node]
  (map second (filter (fn [[from to]] (= from node)) edges)))

(defn dfs [edges f node]
  (loop [stack (vector (vector node #{}))
         visited #{}]
    (if (empty? stack)
      visited
      (let [[curr seen]  (peek stack)
            neighbors (f edges curr)
            minus-already-seen (filter #(not (contains? seen %)) neighbors)]
        (recur (into (pop stack) (map #(vector % (conj seen curr)) minus-already-seen)) (conj visited curr))))))

(defn p1 [nodes edges count-of-pairs]
  (loop [n nodes
         e (vec (take (* 2 count-of-pairs) edges))   ;; since we are including both from-to and to-from edges, we  must multiply by 2
         circuits []]
    (if (empty? n)
      (apply * (take 3 (sort > circuits)))
      (let [next-circuit (dfs e successors (first n))
            n-minus-new-circuit (filter #(not (contains? next-circuit %)) n)
            e-minus-new-circuit (filter #(not (contains? next-circuit (first %))) e)]
        (recur n-minus-new-circuit e-minus-new-circuit (conj circuits (count next-circuit)))))))

(defn- find-component [connected node]
  (first (filter #(contains? % node) connected)))

(defn- add-to-component [connected component node]
  (-> connected
      (disj component)
      (conj (conj component node))))

(defn- merge-components [connected c1 c2]
  (-> connected
      (disj c1)
      (disj c2)
      (conj (s/union c1 c2))))

(defn p2 [edges-seq total-nodes]
  (loop [edges edges-seq
         connected #{}
         last-added nil]
    (if (and (= 1 (count connected)) (= total-nodes (count (first connected))))
      (apply * (map first last-added))
      (let [[from to] (first edges)
            from-component (find-component connected from)
            to-component (find-component connected to)]
        (recur (drop 2 edges)
               (cond
                 (and (nil? from-component) (nil? to-component))
                 (conj connected #{from to})

                 (and from-component (nil? to-component))
                 (add-to-component connected from-component to)

                 (and (nil? from-component) to-component)
                 (add-to-component connected to-component from)

                 (= from-component to-component)
                 connected

                 :else
                 (merge-components connected from-component to-component))
               [from to])))))

(defn -main []
  (let [nodes (parse-input "resources/day8.txt")
        edges (edges-seq nodes)]
    (time (println "Part 2: " (p2 edges (count nodes))))))
