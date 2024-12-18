(ns day18.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-input [input]
  (->> input
       s/split-lines
       (map #(s/split % #","))
       (map (fn [[x y]] [(Integer/parseInt x) (Integer/parseInt y)]))))

(defn within-bounds [grid-size [x y]]
  (and (>= x 0) (< x grid-size) (>= y 0) (< y grid-size)))

(defn successors [walls [x y]]
  (let [walls-set (set walls)]
    (reduce (fn [acc point] (assoc acc point 1)) {}
            (filter #(not (contains? walls-set %))
                    (filter (partial within-bounds 71)
                            (for [[dx dy] [[0 1] [0 -1] [1 0] [-1 0]]]
                              [(+ x dx) (+ y dy)]))))))

(defn map-vals [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn remove-keys [m pred]
  (select-keys m (filter (complement pred) (keys m))))

(defn dijkstra
  "Computes single-source shortest path distances in a directed graph.

  Given a node n, (f n) should return a map with the successors of n
  as keys and their (non-negative) distance from n as vals.

  Returns a map from nodes to their distance from start."
  [start f]
  (loop [q (priority-map start 0) r {}]
    (if-let [[v d] (peek q)]
      (let [dist (-> (f v) (remove-keys r) (map-vals (partial + d)))]
        (recur (merge-with min (pop q) dist) (assoc r v d)))
      r)))

(defn -main
  []
  (let [coords (parse-input (slurp "resources/input.txt"))
        start [0 0]
        end [70 70]
        answer-p2 (+ 3001 (count (take-while some? (map #(get (dijkstra start (partial successors (take % coords))) end) (range 3001 (count coords))))))]
    (println "Part 1:" (get (dijkstra start (partial successors (take 1024 coords))) end))
    (println "Part 2:" (last (take answer-p2 coords)))))