(ns day20.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-grid
  [filename]
  (let [lines (s/split-lines (slurp filename))]
    (->> (for [y (range (count lines))
               x (range (count (nth lines y)))
               :let [c (nth (nth lines y) x)]
              ;;  :when (not= \. c)
               ]
           {c [x y]})
         (reduce (fn [acc m]
                   (let [[k v] (first m)]
                     (update acc k (fnil conj #{}) v)))
                 {}))))

(defn within-bounds [grid-size [x y]]
  (and (>= x 0) (< x grid-size) (>= y 0) (< y grid-size)))

(defn successors [walls [x y]]
  (let [walls-set (set walls)]
    (reduce (fn [acc point] (assoc acc point 1)) {}
            (filter #(not (contains? walls-set %))
                    (filter (partial within-bounds 141)
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

(defn manhattan [a b]
  (apply + (map (fn [[x y]] (Math/abs (- x y))) (map vector a b))))

(defn cheatcode-score [dist goal start end]
  (let [dist-to-goal (dist goal)
        dist-to-end (- dist-to-goal (dist end))]
    (+ (dist start) (manhattan start end) dist-to-end)))

(defn part2 [grid]
  (let [start (first (grid \S))
        goal (first (grid \E))
        walls (grid \#)
        track (grid \.)
        dist (dijkstra start (partial successors walls))
        shortest-path (dist goal)]
    (println "Shortest path: " shortest-path)
    (count (filter #(<= % (- shortest-path 100)) (for [s (conj track start goal)
                                                       e (conj track start goal)
                                                       :when (and (not= s e) (>= 20 (manhattan s e)))]
                                                   (cheatcode-score dist goal s e))))))

(defn -main []
  (let [input (parse-grid "resources/input.txt")]
    (time (println "Part 2: " (part2 input)))))