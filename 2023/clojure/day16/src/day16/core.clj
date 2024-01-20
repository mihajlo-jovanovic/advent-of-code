(ns day16.core
  (:require [clojure.math :as math]
            [clojure.set :as set]
            [clojure.string :as string]))

(defn parse-input
  [input]
  (let [parse-line (fn [[y line]] (map-indexed (fn [x c] {:x x :y y :c c}) line))]
    (->> input
         string/split-lines
         (map-indexed vector)
         (map parse-line)
         flatten
         set)))

(defn next-loc [grid {:keys [x y d] :as p}]
  (case d
    :right (let [n (:c (first (filter #(and (= (inc x) (:x %)) (= y (:y %))) grid)))]
             (case n
               (\. \-) (assoc p :x (inc x))
               \\ {:x (inc x) :y y :d :down}
               \/ {:x (inc x) :y y :d :up}
               \| [{:x (inc x) :y y :d :down} {:x (inc x) :y y :d :up}]
               nil))
    :left (let [n (:c (first (filter #(and (= (dec x) (:x %)) (= y (:y %))) grid)))]
            (case n
              (\. \-) (assoc p :x (dec x))
              \\ {:x (dec x) :y y :d :up}
              \/ {:x (dec x) :y y :d :down}
              \| [{:x (dec x) :y y :d :up} {:x (dec x) :y y :d :down}]
              nil))
    :up (let [n (:c (first (filter #(and (= x (:x %)) (= (dec y) (:y %))) grid)))]
          (case n
            (\. \|) (assoc p :y (dec y))
            \\ {:x x :y (dec y) :d :left}
            \/ {:x x :y (dec y) :d :right}
            \- [{:x x :y (dec y) :d :right} {:x x :y (dec y) :d :left}]
            nil))
    :down (let [n (:c (first (filter #(and (= x (:x %)) (= (inc y) (:y %))) grid)))]
            (case n
              (\. \|) (assoc p :y (inc y))
              \\ {:x x :y (inc y) :d :right}
              \/ {:x x :y (inc y) :d :left}
              \- [{:x x :y (inc y) :d :right} {:x x :y (inc y) :d :left}]
              nil))))

(defn part1 [grid start]
  (loop [beams [start]
         seen #{}]
    (if (empty? beams)
      (dec (count (into #{} (mapv #(vector (:x %) (:y %)) seen))))
      (let [next-beams (remove nil? (flatten (map #(next-loc grid %) beams)))]
        (recur (filter #(not (contains? seen %)) next-beams) (set/union seen (into #{} beams)))))))

(defn part2
  [grid]
  (let [f (partial part1 grid)
        grid-sz (int (math/sqrt (count grid)))
        mx-right (apply max (map f (map #(assoc {} :x -1 :y % :d :right) (range grid-sz))))
        mx-left (apply max (map f (map #(assoc {} :x grid-sz :y % :d :left) (range grid-sz))))
        mx-down (apply max (map f (map #(assoc {} :x % :y -1 :d :down) (range grid-sz))))
        mx-up (apply max (map f (map #(assoc {} :x % :y grid-sz :d :up) (range grid-sz))))]
    (apply max [mx-right mx-left mx-down mx-up])))
