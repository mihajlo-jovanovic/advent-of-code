(ns day16.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-grid
  [filename]
  (let [lines (s/split-lines (slurp filename))]
    (->> (for [y (range (count lines))
               x (range (count (nth lines y)))
               :let [c (nth (nth lines y) x)]
               :when (not= \. c)]
           {c [x y]})
         (reduce (fn [acc m]
                   (let [[k v] (first m)]
                     (update acc k (fnil conj #{}) v)))
                 {}))))

(defn successors [walls {:keys [pos dir]}]
  (let [[x y] pos
        filter-valid (fn [possible]
                       (select-keys possible
                                    (for [[k _] possible
                                          :when (not (contains? walls (:pos k)))] k)))]
    (case dir
      :n (filter-valid {{:pos [x (dec y)] :dir :n} 1
                        {:pos [(inc x) y] :dir :e} 1001
                        {:pos [(dec x) y] :dir :w} 1001})
      :s (filter-valid {{:pos [x (inc y)] :dir :s} 1
                        {:pos [(inc x) y] :dir :e} 1001
                        {:pos [(dec x) y] :dir :w} 1001})
      :e (filter-valid {{:pos [(inc x) y] :dir :e} 1
                        {:pos [x (dec y)] :dir :n} 1001
                        {:pos [x (inc y)] :dir :s} 1001})
      :w (filter-valid {{:pos [(dec x) y] :dir :w} 1
                        {:pos [x (dec y)] :dir :n} 1001
                        {:pos [x (inc y)] :dir :s} 1001}))))

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
  (let [input (parse-grid "resources/input.txt")
        walls (get input \#)
        end (first (get input \E))
        start {:pos (first (get input \S)) :dir :e}
        d (dijkstra start (partial successors walls))
        best-path-cost (get d {:pos end :dir :n})
        d2  (filter #(< (val %) best-path-cost) d)
        ans (->> d2
                 (filter (fn [[k v]]
                           (let [c (get (dijkstra k (partial successors walls)) {:pos end :dir :n})]
                             (and (some? c) (= best-path-cost (+ v c))))))
                 (map #(first (key %)))
                 (into #{})
                 count)]
    (println "Part 1: " best-path-cost)
    (println "Part 2: " (inc ans))))