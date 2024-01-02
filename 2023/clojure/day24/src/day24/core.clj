(ns day24.core
  (:require [clojure.string :as str]
            [clojure.math.combinatorics :as combo]
            [clojure.core.matrix :as matrix]
            [clojure.core.matrix.linear :as ml])
  (:gen-class))

(matrix/set-current-implementation :vectorz)

(defn parse-input [input]
  (map (fn [line]
         (let [[pos-part vel-part] (map str/trim (str/split line #"@"))
               pos-nums (mapv #(bigint (str/trim %)) (str/split pos-part #","))
               vel-nums (mapv #(bigint (str/trim %)) (str/split vel-part #","))]
           (zipmap [:px :py :pz :vx :vy :vz] (concat pos-nums vel-nums))))
       (str/split-lines input)))

(defn normalize-range [range]
  (if (> (first range) (second range))
    [(second range) (first range)]
    range))

(defn normalize-range2 [range]
  (if (> (first (first range)) (first (second range)))
    [(second range) (first range)]
    range))

(defn ranges-overlap? [range1 range2]
  (let [[start1 end1] (normalize-range range1)
        [start2 end2] (normalize-range range2)]
    (not (or (> start1 end2) (> start2 end1)))))

(defn get-y
  "Returns the y value of the line at the given x value, or nil if point is in the `past` (t < 0)"
  [h x]
  (if (or (and (pos? (:vx h)) (> (:px h) x)) (and (neg? (:vx h)) (< (:px h) x)))
    nil
    (+ (:py h) (* (/ (abs (- (:px h) x)) (abs (:vx h))) (:vy h)))))

(defn get-x
  "Returns the x value of the line at the given y value, or nil if point is in the `past` (t < 0)"
  [h y]
  (if (or (and (pos? (:vy h)) (> (:py h) y)) (and (neg? (:vy h)) (< (:py h) y)))
    nil
    (+ (:px h) (* (/ (abs (- (:py h) y)) (abs (:vy h))) (:vx h)))))

(defn find-min-max-x
  "Returns a vector of two points (represented as vectors of size 2) that represent the stretch of the line that is within the test area"
  [area h]
  (let [mn (first area)
        mx (second area)
        points [[(get-x h mn) mn] [(get-x h mx) mx] [mn (get-y h mn)] [mx (get-y h mx)]]
        intersect-test-area (filter #(and (not (nil? (first %))) (not (nil? (second %))) (<= mn (first %) mx) (<= mn (second %) mx)) points)]
    (if (= 2 (count intersect-test-area))
      intersect-test-area
      (conj intersect-test-area [(:px h) (:py h)]))))

(defn outside-test-range [area h]
  (let [mn (first area)
        mx (second area)
        points [[(get-x h mn) mn] [(get-x h mx) mx] [mn (get-y h mn)] [mx (get-y h mx)]]
        intersect-test-area (filter #(and (not (nil? (first %))) (not (nil? (second %))) (<= mn (first %) mx) (<= mn (second %) mx)) points)]
    (empty? intersect-test-area)))

(defn intersect?
  "Returns true if the two lines intersect at any point in time withing the test area range, false otherwise"
  [range h1 h2]
  (if (or (outside-test-range range h1) (outside-test-range range h2))
    false
    (let [b1 (normalize-range2 (find-min-max-x range h1))
          b2 (normalize-range2 (find-min-max-x range h2))
          x1 (max (first (first b1)) (first (first b2)))
          x2 (min (first (second b1)) (first (second b2)))
          y1 (get-y h1 x1)
          y12 (get-y h1 x2)
          y2 (get-y h2 x1)
          y22 (get-y h2 x2)]
      (if (ranges-overlap? [(first (first b1)) (first (second b1))] [(first (first b2)) (first (second b2))])
        (or (and (> y1 y2) (< y12 y22)) (and (< y1 y2) (> y12 y22)))
        false))))

(defn p1 [input range]
  (count (filter #(intersect? range (first %) (second %)) (combo/combinations input 2))))

(defn solve-3-stones
  [{x0 :px  y0 :py z0 :pz vx0 :vx vy0 :vy vz0 :vz}
   {x1 :px  y1 :py z1 :pz vx1 :vx vy1 :vy vz1 :vz}
   {x2 :px  y2 :py z2 :pz vx2 :vx vy2 :vy vz2 :vz}]
  (let [eq [[0 (- vz0 vz1) (- vy1 vy0) 0 (- z1 z0) (- y0 y1)]
            [(- vz1 vz0) 0 (- vx0 vx1) (- z0 z1) 0 (- x1 x0)]
            [(- vy0 vy1) (- vx1 vx0) 0 (- y1 y0) (- x0 x1) 0]
            [0 (- vz0 vz2) (- vy2 vy0) 0 (- z2 z0) (- y0 y2)]
            [(- vz2 vz0) 0 (- vx0 vx2) (- z0 z2) 0 (- x2 x0)]
            [(- vy0 vy2) (- vx2 vx0) 0 (- y2 y0) (- x0 x2) 0]]
        idpx0 (- (* y0 vz0) (* vy0 z0))
        idpx1 (- (* y1 vz1) (* vy1 z1))
        idpx2 (- (* y2 vz2) (* vy2 z2))
        idpy0 (- (* z0 vx0) (* vz0 x0))
        idpy1 (- (* z1 vx1) (* vz1 x1))
        idpy2 (- (* z2 vx2) (* vz2 x2))
        idpz0 (- (* x0 vy0) (* vx0 y0))
        idpz1 (- (* x1 vy1) (* vx1 y1))
        idpz2 (- (* x2 vy2) (* vx2 y2))
        sol [(- idpx0 idpx1)
             (- idpy0 idpy1)
             (- idpz0 idpz1)
             (- idpx0 idpx2)
             (- idpy0 idpy2)
             (- idpz0 idpz2)]]
    (ml/solve eq sol)))

(defn p2 [input]
  (long (reduce + (take 3 (solve-3-stones (first input) (second input) (last input))))))

(defn -main
  []
  (let [input (parse-input (slurp "resources/day24.txt"))]
    (println (p1 input [200000000000000 400000000000000]))
    (println (p2 input))))