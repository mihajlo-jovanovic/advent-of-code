(ns day8.core
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [clojure.set :as set])
  (:gen-class))

(defn parse-input [filepath]
  (->> (slurp filepath)
       str/split-lines
       (mapv #(mapv parse-long (str/split % #",")))))

(defn distance [[x1 y1 z1] [x2 y2 z2]]
  (math/sqrt (+ (math/pow (- x1 x2) 2)
                (math/pow (- y1 y2) 2)
                (math/pow (- z1 z2) 2))))

(defn edges-seq [nodes]
  (->> (for [x1 nodes x2 nodes
             :when (not= x1 x2)]
         [(distance x1 x2) [x1 x2]])
       (sort-by first)
       (map second)))

(defn successors [edges node]
  (keep (fn [[from to]] (when (= from node) to)) edges))

(defn dfs [edges f node]
  (loop [stack [[node #{}]]
         visited #{}]
    (if (empty? stack)
      visited
      (let [[curr seen] (peek stack)
            unseen (remove seen (f edges curr))]
        (recur (into (pop stack) (map #(vector % (conj seen curr)) unseen))
               (conj visited curr))))))

(defn p1 [nodes edges count-of-pairs]
  (loop [n nodes
         e (vec (take (* 2 count-of-pairs) edges))
         circuits []]
    (if (empty? n)
      (->> circuits (sort >) (take 3) (apply *))
      (let [next-circuit (dfs e successors (first n))]
        (recur (remove next-circuit n)
               (remove #(next-circuit (first %)) e)
               (conj circuits (count next-circuit)))))))

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
      (conj (set/union c1 c2))))

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
