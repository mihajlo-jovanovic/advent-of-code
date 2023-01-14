(ns day23.core
  (:gen-class)
  (:require
    [clojure.set :as set]
    [clojure.string :as str]))

(defn parse-line [[x s]]
  (->> s
       (map-indexed vector)
       (filter #(= \# (second %)))
       (map (fn [n] {:x x :y (first n)}))))

(defn parse-scan [s]
  (->> s
       str/split-lines
       (map-indexed vector)
       (map parse-line)
       (filter not-empty)
       flatten
       (into [])))

(defn neighbors [e]
  (filter #(not= e %) (for [x [-1 0 1]
                               y [-1 0 1]]
                           {:x (+ x (:x e)) :y (+ y (:y e))})))

(defn overlap? [coll1 coll2]
  (< 0 (count (set/intersection (set coll1) (set coll2)))))

(defn north [e coll]
  (let [neighbors (for [y [-1 0 1]]
                    {:x (dec (:x e)) :y (+ y (:y e))})]
    (if (overlap? coll neighbors)
      nil
      (update e :x dec))))

(defn south [e coll]
  (let [neighbors (for [y [-1 0 1]]
                    {:x (inc (:x e)) :y (+ y (:y e))})]
    (if (overlap? coll neighbors)
      nil
      (update e :x inc))))

(defn west [e coll]
  (let [neighbors (for [x [-1 0 1]]
                    {:x (+ x (:x e)) :y (dec (:y e))})]
    (if (overlap? coll neighbors)
      nil
      (update e :y dec))))

(defn east [e coll]
  (let [neighbors (for [x [-1 0 1]]
                    {:x (+ x (:x e)) :y (inc (:y e))})]
    (if (overlap? coll neighbors)
      nil
      (update e :y inc))))

(defn round [coll num-of-iterations e]
  (let [ops [north south west east]]
    (if (overlap? coll (neighbors e))
      (if-let [op (first (filter #(% e coll) (take 4 (drop (mod num-of-iterations 4) (cycle ops)))))]
        (op e coll)
        e)
      e)))

(defn round2 [coll rnd]
  (let [proposed (map (partial round coll rnd) coll)
        freq (frequencies proposed)]
    (println rnd)
    (map #(if (= 1 (freq (second %))) (second %) ((into [] coll) (first %))) (map-indexed vector proposed))))

(defn part2 [coll]
  "Collection coll is output of parse-scan"
  (loop [c coll
         rnd 0]
    (let [c2 (round2 c rnd)]
      (if (= c c2)
        (inc rnd)
        (recur c2 (inc rnd))))))