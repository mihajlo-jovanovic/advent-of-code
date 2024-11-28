(ns day6.core
  (:gen-class)
  (:require
   [clojure.string :as str]))

(defn parse-coords
  "Parse a file containing x y coordinates into a list of maps"
  [filename]
  (let [lines (str/split-lines (slurp filename))]
    (->> lines
         (map #(str/split % #", "))
         (map (fn [[x y]] [(Integer/parseInt x)  (Integer/parseInt y)])))))


(defn manhattan-distance
  "Calculate the Manhattan distance between two points"
  [[x y] [x2 y2]]
  (+ (Math/abs (- x x2)) (Math/abs (- y y2))))

(defn closest-point [coords point]
  (let [distances (map #(manhattan-distance point %) coords)
        min-distance (apply min distances)]
    (if (= 1 (count (filter #(= % min-distance) distances)))
      (first (filter #(= min-distance (manhattan-distance point %)) coords))
      nil)))

(defn sum-distances [coords point]
  (reduce + (map #(manhattan-distance point %) coords)))

(defn within-range [coords point range]
  (< (sum-distances coords point) range))

(defn part-2 [coords limit]
  (let [mx-x (apply max (map first coords))
        mx-y (apply max (map second coords))]
    (loop [counter (count (filter #(within-range coords % limit) (for [x (range 0 (inc mx-x)) y (range 0 (inc mx-y))] [x y])))
           x-left -1
           x-right (inc mx-x)
           y-top -1
           y-bottom (inc mx-y)]
      (println x-left x-right y-top y-bottom)
      (let [added (+ (count (filter #(within-range coords % limit) (for [x [x-left x-right] y (range y-top (inc y-bottom))] [x y])))
                     (count (filter #(within-range coords % limit) (for [x (range x-left (inc x-right)) y [y-top y-bottom]] [x y]))))]
        (if (= added 0)
          counter
          (recur (+ counter added) (dec x-left) (inc x-right) (dec y-top) (inc y-bottom)))))))

;; (defn -main []
;;   (let [input (parse-coords "resources/input.txt")
;;         mx-x (apply max (map first input))
;;         mx-y (apply max (map second input))
;;         ;; corners [[0 0] [0 mx-y] [mx-x 0] [mx-x mx-y]]
;;         ;; to-exclude (set (map (partial closest-point input) corners))
;;         result1 (into {} (map (fn [[k v]] {k (count v)}) (into {} (filter (comp some? key) (group-by #(closest-point input %) (for [x (range 0 (inc mx-x)) y (range 0 (inc mx-y))] [x y]))))))
;;         result2 (into {} (map (fn [[k v]] {k (count v)}) (into {} (filter (comp some? key) (group-by #(closest-point input %) (for [x (range -50 (+ 50 (inc mx-x))) y (range -50 (+ 50 (inc mx-y)))] [x y]))))))]
;;         ;; result2 (map (fn [[k v]] {k (count v)}) (group-by #(closest-point input %) (for [x (range 0 (+ 50 (inc mx-x))) y (range 0 (+ 50 (inc mx-y)))] [x y])))]
;;     (println "Part 1: " (filter (fn [[k v]] (= v (get result1 k))) result2))))

(defn -main []
  (let [input (parse-coords "resources/input.txt")]
        ;; result2 (map (fn [[k v]] {k (count v)}) (group-by #(closest-point input %) (for [x (range 0 (+ 50 (inc mx-x))) y (range 0 (+ 50 (inc mx-y)))] [x y])))]
    (time (println "Part 2: " (part-2 input 10000)))))