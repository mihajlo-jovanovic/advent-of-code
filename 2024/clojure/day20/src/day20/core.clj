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
               :when (not= \. c)]
           {c [x y]})
         (reduce (fn [acc m]
                   (let [[k v] (first m)]
                     (update acc k (fnil conj #{}) v)))
                 {}))))

;; (defn parse-input [input]
;;   (->> input
;;        s/split-lines
;;        (map #(s/split % #","))
;;        (map (fn [[x y]] [(Integer/parseInt x) (Integer/parseInt y)]))))

(defn within-bounds [grid-size [x y]]
  (and (>= x 0) (< x grid-size) (>= y 0) (< y grid-size)))

(defn successors [walls [x y]]
  (let [walls-set (set walls)]
    (reduce (fn [acc point] (assoc acc point 1)) {}
            (filter #(not (contains? walls-set %))
                    (filter (partial within-bounds 141)
                            (for [[dx dy] [[0 1] [0 -1] [1 0] [-1 0]]]
                              [(+ x dx) (+ y dy)]))))))

;; (def node {:pos [0 0] :can-pass-through-walls? true :start nil})

(defn node-at [pos can-pass-through-walls? start end]
  {:pos pos :can-pass-through-walls? can-pass-through-walls? :start start :end end})

(defn successors-p2 [walls grid-size {:keys [pos can-pass-through-walls? start end]}]
  (let [walls-set (set walls)
        normal-successors (filter (partial within-bounds grid-size)
                                  (for [[dx dy] [[0 1] [0 -1] [1 0] [-1 0]]]
                                    [(+ (first pos) dx) (+ (second pos) dy)]))]
    (reduce (fn [acc point] (assoc acc point 1)) {}
            (cond
              (not can-pass-through-walls?) (map (fn [p] (node-at p false start end)) (filter #(not (contains? walls-set %)) normal-successors))   ;; just filter walls and we're done

              (and can-pass-through-walls? (not (nil? start))) (map #(node-at % false start %) (filter #(not (contains? walls-set %)) normal-successors))  ;; we're in the middle of a wall

              (and can-pass-through-walls? (nil? start)) (map #(if (not (contains? walls-set %)) (node-at % true nil nil) (node-at % true % nil)) normal-successors)))))


;; (defn map-vals [m f]
;;   (into {} (for [[k v] m] [k (f v)])))

;; (defn remove-keys [m pred]
;;   (select-keys m (filter (complement pred) (keys m))))

;; (defn dijkstra
;;   "Computes single-source shortest path distances in a directed graph.

;;   Given a node n, (f n) should return a map with the successors of n
;;   as keys and their (non-negative) distance from n as vals.

;;   Returns a map from nodes to their distance from start."
;;   [start f]
;;   (loop [q (priority-map start 0) r {}]
;;     (if-let [[v d] (peek q)]
;;       (let [dist (-> (f v) (remove-keys r) (map-vals (partial + d)))]
;;         (recur (merge-with min (pop q) dist) (assoc r v d)))
;;       r)))

(defn dijkstra2 [start f]
  (loop [q (priority-map start 0)
         visited #{}  ; track visited nodes
         dist {}]
    (if-let [[v d] (peek q)]
      (if (contains? visited v)
        (recur (pop q) visited dist)
        (let [visited (conj visited v)
              neighbors (f v)]
          (recur
           (reduce-kv (fn [pq nv nd]
                        (if (contains? visited nv)
                          pq
                          (assoc pq nv (min (get pq nv (Long/MAX_VALUE))
                                            (+ d nd)))))
                      (pop q)
                      neighbors)
           visited
           (assoc dist v d))))
      dist)))

(defn -main
  []
  (let [input (parse-grid "resources/input.txt")
        walls (get input \#)
        start (first (get input \S))
        end (first (get input \E))
        start-node (node-at start true nil nil)
        ans (mapv (fn [[k v]] (assoc {} (vector (:start k) (:end k)) v)) (filter #(= end (:pos (key %))) (dijkstra2 start-node (partial successors-p2 walls 141))))
        ans'' (reduce + (map #(count (map ffirst (filter (fn [x] (= (second (first x)) (- 9316 %))) ans))) (range 2 65)))]
    (println "Part 1: " ans'')))
