(ns day17.core
  (:gen-class)
  (:require [clojure.data.priority-map :refer [priority-map]]
            [clojure.math :refer [sqrt]]
            [clojure.string :as string]))

(defn parse-input
  [input]
  (let [parse-ln (fn [y line]
                   (map-indexed #(hash-map [%1 y] (Character/digit %2 10)) line))]
    (apply merge-with into (apply concat (map-indexed parse-ln (string/split-lines input))))))

(defn- ->successors [m coll]
  (->> (filter some? coll)
       (map (fn [[x y _ _ :as k]] (assoc {} k (get m (vector x y)))))
       (apply merge)))

(defn east
  [m max-sz min-d max-d [x y cnt]]
  (let [n (if (and (> y 0) (> cnt min-d)) [x (dec y) :north 1] nil)
        s (if (and (< y max-sz) (> cnt min-d)) [x (inc y) :south 1] nil)
        e (if (and (< cnt max-d) (< x max-sz)) [(inc x) y :east (inc cnt)] nil)]
    (->successors m [n s e])))

(defn west
  [m max-sz min-d max-d [x y cnt]]
  (let [n (if (and (> y 0) (> cnt min-d)) [x (dec y) :north 1] nil)
        s (if (and (< y max-sz) (> cnt min-d)) [x (inc y) :south 1] nil)
        w (if (and (< cnt max-d) (> x 0)) [(dec x) y :west (inc cnt)] nil)]
    (->successors m [n s w])))

(defn north
  [m max-sz min-d max-d [x y cnt]]
  (let [e (if (and (< x max-sz) (> cnt min-d)) [(inc x) y :east 1] nil)
        w (if (and (> x 0) (> cnt min-d)) [(dec x) y :west 1] nil)
        n (if (and (< cnt max-d) (> y 0)) [x (dec y) :north (inc cnt)] nil)]
    (->successors m [e w n])))

(defn south
  [m max-sz min-d max-d [x y cnt]]
  (let [e (if (and (< x max-sz) (> cnt min-d)) [(inc x) y :east 1] nil)
        w (if (and (> x 0) (> cnt min-d)) [(dec x) y :west 1] nil)
        s (if (and (< cnt max-d) (< y max-sz)) [x (inc y) :south (inc cnt)] nil)]
    (->successors m [e w s])))

(defn neighbors
  [m max-sz min-d max-d [x y dir cnt]]
  (case dir
    :east (east m max-sz min-d max-d [x y cnt])
    :west (west m max-sz min-d max-d [x y cnt])
    :north (north m max-sz min-d max-d [x y cnt])
    :south (south m max-sz min-d max-d [x y cnt])
    {[1 0 :east 1] (get m [1 0]) [0 1 :south 1] (get m [0 1])}))

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

(defn solve
  [min-d max-d input]
  (let [max-sz (dec (int (sqrt (count input))))
        f (partial neighbors input max-sz min-d max-d)]
    (->> (dijkstra [0 0 nil nil] f)
         (filter (fn [[k _]] (= max-sz (first k) (second k))))
         (map second)
         (apply min))))

(def part1 (partial solve 0 3))
(def part2 (partial solve 3 10))