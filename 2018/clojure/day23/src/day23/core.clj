(ns day23.core
  (:require [clojure.string :as str])
  (:import (java.util PriorityQueue Comparator)))

(defn parse-input [filepath]
  (->> (slurp filepath)
       clojure.string/split-lines
       (keep (fn [line]
               (when-let [[_ x y z r]
                          (re-matches #"pos=<(-?\d+),(-?\d+),(-?\d+)>,\s*r=(\d+)" line)]
                 [(Long/parseLong x)
                  (Long/parseLong y)
                  (Long/parseLong z)
                  (Long/parseLong r)])))
       vec))

(defn abs ^long [^long x] (long (Math/abs x)))

(defn manhattan-distance
  "Calculate the Manhattan distance between two points"
  [[x y z] [x' y' z']]
  (+ (abs (- x x')) (abs (- y y')) (abs (- z z'))))

(defn p1 [input]
  (let [max-radius (apply max (map last input))
        [x y z r] (some #(when (= max-radius (last %)) %) input)]
    (count (filter (fn [[x' y' z' _]] (>= max-radius (manhattan-distance [x y z] [x' y' z']))) input))))

(defn dist-point-to-interval
  "Min L1 distance from coordinate c to closed interval [lo, hi]."
  ^long [^long c ^long lo ^long hi]
  (cond
    (< c lo) (- lo c)
    (> c hi) (- c hi)
    :else 0))

(defn bot-cube-min-dist
  "Minimum Manhattan distance from bot center to any point in cube.
   Cube is {:x :y :z :size}, covering [x..x+size-1], etc."
  ^long [[^long bx ^long by ^long bz ^long r] {:keys [^long x ^long y ^long z ^long size]}]
  (let [x2 (long (+ x (dec size)))
        y2 (long (+ y (dec size)))
        z2 (long (+ z (dec size)))]
    (+ (dist-point-to-interval bx x x2)
       (dist-point-to-interval by y y2)
       (dist-point-to-interval bz z z2))))

(defn cube-bot-intersects?
  "True if cube could contain some point within bot radius."
  [bot cube]
  (<= (bot-cube-min-dist bot cube) (nth bot 3)))

(defn cube-score
  "Upper bound: how many bots could be in range of *some* point in cube."
  ^long [bots cube]
  (reduce (fn [^long acc bot]
            (if (cube-bot-intersects? bot cube)
              (inc acc)
              acc))
          0
          bots))

(defn cube-min-dist-to-origin
  "Minimum Manhattan distance from any point in cube to (0,0,0)."
  ^long [{:keys [^long x ^long y ^long z ^long size]}]
  (let [x2 (long (+ x (dec size)))
        y2 (long (+ y (dec size)))
        z2 (long (+ z (dec size)))]
    (+ (dist-point-to-interval 0 x x2)
       (dist-point-to-interval 0 y y2)
       (dist-point-to-interval 0 z z2))))

(defn next-power-of-two ^long [^long n]
  (loop [p 1]
    (if (>= p n) p (recur (bit-shift-left p 1)))))

(defn initial-cube
  "Build a power-of-two cube covering all (pos +/- r) extents."
  [bots]
  (let [minx (reduce min (map (fn [[x _ _ r]] (- x r)) bots))
        maxx (reduce max (map (fn [[x _ _ r]] (+ x r)) bots))
        miny (reduce min (map (fn [[_ y _ r]] (- y r)) bots))
        maxy (reduce max (map (fn [[_ y _ r]] (+ y r)) bots))
        minz (reduce min (map (fn [[_ _ z r]] (- z r)) bots))
        maxz (reduce max (map (fn [[_ _ z r]] (+ z r)) bots))
        spanx (inc (- maxx minx))
        spany (inc (- maxy miny))
        spanz (inc (- maxz minz))
        size  (next-power-of-two (long (max spanx spany spanz)))]
    {:x minx :y miny :z minz :size size}))

(defn split-cube
  "Split cube into 8 subcubes (size is always power-of-two so clean halving)."
  [{:keys [^long x ^long y ^long z ^long size]}]
  (let [half (quot size 2)]
    (for [dx [0 half]
          dy [0 half]
          dz [0 half]]
      {:x (+ x dx) :y (+ y dy) :z (+ z dz) :size half})))

(defn make-pq []
  ;; Priority: higher :score first, then smaller :dist, then smaller :size.
  ;; Java PQ is min-heap, so comparator returns negative when a<b.
  (PriorityQueue.
   1024
   (reify Comparator
     (compare [_ a b]
       (let [sa (long (:score a)) sb (long (:score b))]
         (cond
           (not= sa sb) (compare sb sa)                ;; desc score
           (not= (:dist a) (:dist b)) (compare (:dist a) (:dist b)) ;; asc dist
           :else (compare (:size a) (:size b))))))))   ;; asc size

(defn push-cube! [^PriorityQueue pq bots cube]
  (let [sc   (cube-score bots cube)
        dist (cube-min-dist-to-origin cube)]
    (.add pq (assoc cube :score sc :dist dist))))

(defn p2
  "Returns {:x :y :z :dist :score} for best point found (exact with this approach)."
  [bots]
  (let [start (initial-cube bots)
        pq    (make-pq)]
    (push-cube! pq bots start)
    (loop []
      (when-let [cur (.poll pq)]
        (if (= 1 (long (:size cur)))
          ;; size=1 => cube is a single point; since it's popped first, it's optimal
          ;; {:x (:x cur) :y (:y cur) :z (:z cur) :dist (:dist cur) :score (:score cur)}
          (:dist cur)
          (do
            (doseq [c (split-cube cur)]
              (push-cube! pq bots c))
            (recur)))))))
