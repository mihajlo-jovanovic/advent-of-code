(ns day10.core
  (:require [clojure.set :as set]
            [clojure.string :as string])
  (:gen-class))

;; (defn parse-input
;;   [input]
;;   (->> (string/split-lines input)
;;        (map-indexed vector)
;;        (map (fn [[y line]] (->> (map-indexed vector line)
;;                                 (filter #(not (= \. (second %))))
;;                                 (map #(assoc {} :x (first %) :y y :value (second %))))))
;;        (apply set/union)
;;        (into #{})))

(defn parse-input [input]
  (let [lines (string/split-lines input)
        rows (count lines)
        process-tile (fn [y x tile]
                       (when (not= \. tile)
                         {:y y, :x x, :pipe tile}))]
    (set (for [y (range rows)
               x (range (count (lines y)))
               :let [tile (nth (lines y) x)]
               :when (not= \. tile)]
           (process-tile y x tile)))))

(defn connected?
  [tile1 tile2]
  (let [x1 (:x tile1)
        y1 (:y tile1)
        x2 (:x tile2)
        y2 (:y tile2)
        v1 (:pipe tile1)
        v2 (:pipe tile2)]
    (cond (and (= x1 (inc x2)) (= y1 y2)) (and (contains? #{\- \J \7 \S} v1) (contains? #{\- \F \L \S} v2))
          (and (= x1 (dec x2)) (= y1 y2)) (and (contains? #{\- \F \L \S} v1) (contains? #{\- \J \7 \S} v2))
          (and (= y1 (inc y2)) (= x1 x2)) (and (contains? #{\| \L \J \S} v1) (contains? #{\| \7 \F \S} v2))
          (and (= y1 (dec y2)) (= x1 x2)) (and (contains? #{\| \7 \F \S} v1) (contains? #{\| \L \J \S} v2)))))

(defn neighbors
  [tile input-set]
  (filter #(or
            (and (= (inc (:x tile)) (:x %)) (= (:y tile) (:y %)))
            (and (= (dec (:x tile)) (:x %)) (= (:y tile) (:y %)))
            (and (= (inc (:y tile)) (:y %)) (= (:x tile) (:x %)))
            (and (= (dec (:y tile)) (:y %)) (= (:x tile) (:x %))))  input-set))

(defn p1
  [input-set]
  (loop [seen #{}
         current (first (filter #(= \S (:pipe %)) input-set))]
    (let [neighbors (filter (partial connected? current) input-set)]
      (if (and (= 2 (count neighbors)) (empty? seen))
        (recur (conj seen current) (first neighbors))
        (let [not-yet-seen (filter #(not (contains? seen %)) neighbors)]
          (if (empty? not-yet-seen)
            (/ (inc (count seen)) 2)
            (recur (conj seen current) (first not-yet-seen))))))))

;; part 2

(defn counter-clockwise
  [l current]
  (if (= 1 (count l))
    (first l)
    (let [y (:y current)
          next (filter #(= (inc y) (:y %)) l)]
      (if (empty? next)
        (first l)
        (first next)))))

(defn next-tile
  "Returns the next tile in the path; if there and no next tile, returns nil. If this is the first tile, returns the one that is below (counter-clokwise)"
  [current previous grid]
  (let [next (filter #(connected? current %) (neighbors current grid))]
    (if (nil? previous)
      (counter-clockwise next current)
      (first (filter #(not= previous %) next)))))


(defn main-loop
  "Returns main loop path an a vector (ordered list) of tiles"
  [start-tile grid]
  (loop [path []
         current start-tile]
    (let [next (next-tile current (last path) grid)]
      (if (= next start-tile)
        (conj path current)
        (recur (conj path current) next)))))

(defn get-inside-neighbor
  "Given two points representing path segment in counter clockwise direction, returns the inside neighbours of the first tile"
  [[x1 y1] [x2 y2]]
  (cond (and (= x1 x2) (= y1 (inc y2))) [[(dec x1) y1] [(dec x2) y2]]   ;; north
        (and (= x1 x2) (= y1 (dec y2))) [[(inc x1) y1] [(inc x2) y2]]   ;; south
        (and (= y1 y2) (= x1 (inc x2))) [[x1 (inc y1)] [x2 (inc y2)]]   ;; west
        (and (= y1 y2) (= x1 (dec x2))) [[x1 (dec y1)] [x2 (dec y2)]])) ;; east

(defn enclosed
  [loop-coords acc c]
  (if (>= (inc c) (count loop-coords))
    acc
    (let [first (nth loop-coords c)
          second (nth loop-coords (inc c))
          inside (get-inside-neighbor first second)
          loop-coords-set (into #{} loop-coords)
          enc (filter #(not (contains? loop-coords-set %)) inside)]
      (if (empty? enc)
        (recur loop-coords acc (inc c))
        (recur loop-coords (clojure.set/union acc (into #{} enc)) (inc c))))))

(defn get-cluster
  [loop-coords [x y]]
  (let [n [x (dec y)]
        s [x (inc y)]
        w [(dec x) y]
        e [(inc x) y]
        loop-coords-set (into #{} loop-coords)]
    (into #{[x y]} (filter #(not (contains? loop-coords-set %)) [n s w e]))))

(defn first-duplicate [coll]
  (reduce (fn [acc x]
            (if (contains? acc x)
              (reduced x)
              (conj acc x)))
          #{} coll))

(defn p2
  "Given a loop path (represented as a vector of x y coordinates), returns the number of enclosed tiles"
  [loop-coords]
  (let [first-layer (enclosed loop-coords #{} 0)
        next-layer (fn [coll] (reduce clojure.set/union (map #(get-cluster loop-coords %) coll)))]
    (first-duplicate (map count (take 100 (iterate next-layer first-layer))))))

(defn -main
  []
  (let [input (parse-input (slurp "resources/day10.txt"))
        start-tile (first (filter #(= \S (:pipe %)) input))
        loop-tiles (main-loop start-tile input)
        loop-coords-only (map #(vector (:x %) (:y %)) loop-tiles)]
    (println "Part 1: " (p1 input))
    (println "Part 2: " (p2 loop-coords-only))))