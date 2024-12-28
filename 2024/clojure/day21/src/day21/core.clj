(ns day21.core
  (:gen-class)
  (:require [clojure.string :as s]))

(def numeric-keypad {\7 [-2 -3]
                     \8 [-1 -3]
                     \9 [0 -3]
                     \4 [-2 -2]
                     \5 [-1 -2]
                     \6 [0 -2]
                     \1 [-2 -1]
                     \2 [-1 -1]
                     \3 [0 -1]
                     \0 [-1 0]
                     \A [0 0]})

(def directional-keypad {\^ [-1 0]
                         \A [0 0]
                         \< [-2 1]
                         \v [-1 1]
                         \> [0 1]})

(def gap [-2 0])

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs (- x2 x1)) (Math/abs (- y2 y1))))

(defn shortest-length-directional
  "Return the shortest length of a sequence of moves  from start to end on a single 
   directional keypad using Manhattan distance steps."
  [start end]
  (let [[x1 y1] (directional-keypad start)
        [x2 y2] (directional-keypad end)]
    (inc (manhattan-distance [x1 y1] [x2 y2]))))

(defn shortest-sequence
  "Converts to a string the shortest sequence of moves from [x1 y1] to [x2 y2]
   using only the characters '<', '>', '^', 'v'."
  [[x1 y1] [x2 y2]]
  (let [move-x (- x2 x1)
        move-y (- y2 y1)]
    (str (apply str (repeat (Math/abs move-x) (if (pos? move-x) \> \<)))
         (apply str (repeat (Math/abs move-y) (if (pos? move-y) \v \^))))))

(defn choose
  "Return a sequence of all ways to choose `k` elements from sequence `s`.
   (Lazy version of combinations if you don't want to pull in
   clojure.math.combinatorics.)"
  [k s]
  (cond
    (zero? k) (list ())
    (empty? s) '()
    :else
    (let [head (first s)
          tail (rest s)]
      (concat
       ;; subsets that include `head`
       (map (partial cons head)
            (choose (dec k) tail))
       ;; subsets that do not include `head`
       (choose k tail)))))

(defn all-shortest-paths
  "Return a sequence of all possible shortest paths (each path is a sequence
   of [x y] coordinates) from start to end using Manhattan distance steps."
  [[sx sy :as start] [ex ey]]
  (let [dx        (- ex sx)
        dy        (- ey sy)
        steps-x   (Math/abs dx)
        steps-y   (Math/abs dy)
        n         (+ steps-x steps-y)     ;; total steps
        x-step-fn (if (pos? dx) (fn [[x y]] [(inc x) y])
                      (fn [[x y]] [(dec x) y]))
        y-step-fn (if (pos? dy) (fn [[x y]] [x (inc y)])
                      (fn [[x y]] [x (dec y)]))]

    ;; For each subset of size `steps-x` in the range 0..(n-1),
    ;; those positions will be 'x-steps', and the rest 'y-steps'.
    (for [x-positions (choose steps-x (range n))]
      (reduce
       (fn [path step-idx]
         (let [current-pos (peek path)]
           (if (some #{step-idx} x-positions)
             (conj path (x-step-fn current-pos))
             (conj path (y-step-fn current-pos)))))
       [start]
       (range n)))))

(defn combine-strings [coll1 coll2]
  (for [x coll1
        y coll2]
    (str x y \A)))

(defn input-sequence [keypad code]
  (let [[final-sequences _current-position]
        (reduce (fn [[acc-sequence position] element]
                  (let [new-sequences (filter (fn [coll] (every? #(not= gap %) coll)) (all-shortest-paths position (keypad element)))
                        new-sequences' (map (fn [x] (apply str (map #(shortest-sequence (first %) (second %)) (partition 2 1 x)))) new-sequences)]
                    [(combine-strings acc-sequence new-sequences') (keypad element)]))
                [[""] (keypad \A)]
                (char-array code))]
    final-sequences))

(defn all-possible [start end]
  (let [new-sequences (filter (fn [coll] (every? #(not= gap %) coll)) (all-shortest-paths (directional-keypad start) (directional-keypad end)))
        new-sequences' (map (fn [x] (apply str (map #(shortest-sequence (first %) (second %)) (partition 2 1 x)))) new-sequences)]
    new-sequences'))

;; (defn distance
;;   "Calculate distance between two positions on a set of n directional keypads"
;;   [start end n]
;;   (if (zero? n)
;;     (shortest-length-directional start end)
;;     (let [all-paths (map #(partition 2 1 (str \A % \A)) (all-possible start end))]
;;       (apply min (map #(reduce (fn [acc [start2 end2]] (+ acc (distance start2 end2 (dec n)))) 0 %) all-paths)))))

(def m-distance
  "Calculate distance between two positions on a set of n directional keypads"
  (memoize
   (fn [start end n]
     (if (zero? n)
       (shortest-length-directional start end)
       (let [all-paths (map #(partition 2 1 (str \A % \A)) (all-possible start end))]
         (apply min (map #(reduce (fn [acc [start end]] (+ acc (m-distance start end (dec n)))) 0 %) all-paths)))))))

(defn solve [coll n]
  (reduce (fn [acc code]
            (let [sequences (input-sequence numeric-keypad code)
                  parts (map #(partition 2 1 (str \A %)) sequences)
                  min-length (apply min (map #(reduce (fn [acc [start end]] (+ acc (m-distance start end (dec n)))) 0 %) parts))
                  numeric-part (Integer/parseInt (.substring code 0 (dec (count code))))]
              (+ acc (* min-length numeric-part)))) 0 coll))

(defn -main []
  (let [input (s/split-lines (slurp "resources/input.txt"))]
    (println "Part 1: " (solve input 2))
    (time (println "Part 2: " (solve input 25)))))