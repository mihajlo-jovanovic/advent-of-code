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

;; (def current-position [2 3])
(def gap [-2 0])

(defn shortest-sequence [[x1 y1] [x2 y2]]
  (let [move-x (- x2 x1)
        move-y (- y2 y1)]
    (str (apply str (repeat (Math/abs move-x) (if (pos? move-x) \> \<)))
         (apply str (repeat (Math/abs move-y) (if (pos? move-y) \v \^))))))

;; (defn find-path-dfs [[x1 y1] [x2 y2]]
;;   (loop [stack (vector [x1 y1])
;;          result #{}]
;;     (if (empty? stack)
;;       result
;;       (let [[x y] (peek stack)
;;             new-stack (pop stack)
;;             move-y (if (pos? (- y2 y)) 1 -1)
;;             new-paths (if (= x x2) [] [[x y] [(+ x move-x) y]])
;;             new-paths' (if (= y y2) new-paths (vector [[x y] [x (+ y move-y)]] new-paths))]
;;         (recur new-stack (into result new-paths'))))))

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
  [[sx sy :as start] [ex ey :as end]]
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

;; to find all valid paths from start [2 3] to end [0 0] point:
;; (def tmp (filter (fn [coll] (every? #(not= [0 3] %) coll)) (all-shortest-paths [2 3] [0 0])))
;; (map (fn [coll] (apply str (map (fn [x] (shortest-sequence (first x) (second x))) coll))) (map #(partition 2 1 %) tmp))

;; (defn input-sequence [code]
;;   (let [[final-sequence _current-position]
;;         (reduce (fn [[acc-sequence position] element]
;;                   (let [new-sequence (shortest-sequence position (numeric-keypad element))]
;;                     [(str acc-sequence new-sequence \A) (numeric-keypad element)]))
;;                 ["" (numeric-keypad \A)]
;;                 (map #(Integer/parseInt (str %)) (drop-last code)))]
;;     (str final-sequence (shortest-sequence _current-position (numeric-keypad (last code))) \A)))

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

(defn part1 [coll]
  (reduce (fn [acc code]
            (let [sequences (input-sequence numeric-keypad code)
                  sequences' (flatten (map #(input-sequence directional-keypad %) sequences))
                  sequences'' (flatten (map #(input-sequence directional-keypad %) sequences'))
                  ;; sequences''' (flatten (map #(input-sequence directional-keypad %) sequences''))
                  lenght-of-shortest-sequence (apply min (map count sequences''))
                  numeric-part (Integer/parseInt (.substring code 0 (dec (count code))))]
              ;; (println code numeric-part lenght-of-shortest-sequence)
              (+ acc (* lenght-of-shortest-sequence numeric-part)))) 0 coll))

(defn -main
  []
  (time (println "Part 1: " (part1 (s/split-lines (slurp "resources/input.txt"))))))