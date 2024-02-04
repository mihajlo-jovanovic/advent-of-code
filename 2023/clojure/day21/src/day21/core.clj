(ns day21.core
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [clojure.core :as c]))

(defn parse-input
  [input]
  (let [parse-line (fn [[y line]] (map-indexed (fn [x c] {:x x :y y :c c}) line))]
    (->> input
         string/split-lines
         (map-indexed vector)
         (map parse-line)
         flatten
         set)))

;; (def parsed (into #{} (map #(vector (:x %) (:y %)) (filter (fn [{:keys [:x :y :c]}] (not= c \#)) (parse-input (slurp "resources/day21.txt"))))))

(defn neighbors [coll [x y]]
  (let [d [[-1 0] [1 0] [0 -1] [0 1]]
        neighbors (map (fn [[dx dy]] [(+ x dx) (+ y dy)]) d)]
    (filter (fn [[x y]] (contains? coll (vector (mod x 131) (mod y 131)))) neighbors)))  ;; mod value hard-coded; remove for part 1

(defn step [f coll]
  (->> (map f coll)
       (apply concat)
       set))

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs (- x1 x2)) (Math/abs (- y1 y2))))

(defn all-points-within-distance [distance [x y]]
  (for [i (range (- distance) (inc distance))
        j (range (- distance) (inc distance))
        :when (= (manhattan-distance [0 0] [i j]) distance)]
    [(+ x i) (+ y j)]))

(defn idx-fully-covered [coll all-odd start]
  (let [s (iterate (partial step (partial neighbors coll)) start)
        all-odd (set all-odd)]
    (loop [s2 s
           i 0]
      (if (set/superset? (first s2) all-odd)
        i
        (recur (rest s2) (inc i))))))

(defn quadrant [[x y]]
  (let [f (fn [n] (if (neg? n) (- (quot n 11) 1) (quot n 11)))]
    (vector (f x) (f y))))

(defn p2 [input n]
  (let [start #{[5 5]}
        f (partial step (partial neighbors input))]
    (loop [s (f start)
           i 1
           complete #{}]
      (if (>= i n)
        (+ (count (filter #(not (contains? complete (quadrant %))) s)) (if (odd? n) (* 42 (count complete)) (* 40 (count complete))))
        (let [s-new (f s)
              complete-new (clojure.set/union complete (into #{} (keys (filter (fn [[_ v]] (= 81 (count v))) (merge-with concat (group-by quadrant s) (group-by quadrant s-new))))))]
          ;;  (println complete-new)
          (recur (filter #(not (contains? complete-new (quadrant %))) s-new) (inc i) complete-new))))))

;;  (into {} (for [[k v] (group-by (fn [[x y]] (vector (quot x 11) (quot y 11))) parsed)] [k (count v)]))

(def edge-west (into #{} (for [x '(0) y (range 0 11)] [x y])))
(def edge-east (into #{} (for [x '(10) y (range 0 11)] [x y])))
(def edge-north (into #{} (for [x (range 0 11) y '(0)] [x y])))
(def edge-south (into #{} (for [x (range 0 11) y '(10)] [x y])))

(def max-odd 39)
(def max-even 42)

(defn solve
  "Returns number of plots reachable in n steps. start is a set of coordinates represented as vector of two numbers."
  [plots n start]
  ;; (println "start: " start)
  (if (zero? n)
    (count start)
    (loop [next (step (partial neighbors plots) start)
           i n
           l 0
           r 0
           t 0
           b 0]
      (if (= 1 i)
        (+ (count next) l r t b)
        (let [l-edge (filter #(contains? edge-west %) next)
              l-new (if (and (seq l-edge) (zero? l) (empty? (filter #(contains? edge-west %) start))) (solve plots (dec (dec i))  (into #{} (for [[_ v] l-edge] [10 v]))) l)
              r-edge (filter #(contains? edge-east %) next)
              r-new (if (and (seq r-edge) (zero? r) (empty? (filter #(contains? edge-east %) start))) (solve plots (dec (dec i))  (into #{} (for [[_ v] r-edge] [0 v]))) r)
              t-edge (filter #(contains? edge-north %) next)
              t-new (if (and (seq t-edge) (zero? t) (empty? (filter #(contains? edge-north %) start))) (solve plots (dec (dec i))  (into #{} (for [[k _] t-edge] [k 10]))) t)
              b-edge (filter #(contains? edge-south %) next)
              b-new (if (and (seq b-edge) (zero? b) (empty? (filter #(contains? edge-south %) start))) (solve plots (dec (dec i))  (into #{} (for [[k _] b-edge] [k 0]))) b)]
          ;; (println i l-new r-new t-new b-new)
          (recur (step (partial neighbors plots) next) (dec i) l-new r-new t-new b-new))))))

(defn lagrange
  [y0 y1 y2]
  [(+ (/ y0 2) (- y1) (/ y2 2))
   (+ (* -3 (/ y0 2)) (* 2 y1) (- (/ y2 2)))
   y0])

(defn part-two
  "using lagrange - following
   /r/adventofcode/comments/18nevo3/2023_day_21_solutions/keb6a53/"
  ([]
   (let [[y0 y1 y2] [3701
                     33108
                     91853]
         [x0 x1 x2] (lagrange y0 y1 y2)
         target (/ (- 26501365 65) 131)]
     (+ (* x0 target target)
        (* x1 target)
        x2))))