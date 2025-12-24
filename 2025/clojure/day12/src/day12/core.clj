(ns day12.core
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn parse-input [filepath]
  (let [parts (str/split (slurp filepath) #"\n\n")
        lines (->> (take 6 parts) (map str/split-lines) (map #(drop 1 %)))
        parse-regions (fn [region] (let [[f s & r]  (mapv Integer/parseInt (re-seq #"\d+" region))] {:max-x f :max-y s :quantities (vec r)}))]
    {:gifts (mapv #(set (for [y (range (count %)) x (range (count (nth % y))) :when  (= "#" (str (nth (nth % y) x)))] [x y])) lines)
     :regions (map parse-regions (str/split-lines (last parts)))}))

;; The 3x3 grid constraints
(def max-idx 2)

(defn rotate-cw [[x y]]
  [y (- max-idx x)])

(defn rotate-ccw [[x y]]
  [(- max-idx y) x])

(defn flip-horiz [[x y]]
  [(- max-idx x) y])

(defn flip-vert [[x y]]
  [x (- max-idx y)])

(defn offset-x [n [x y]]
  [(+ n x) y])

(defn offset-y [n [x y]]
  [x (+ n y)])

(defn transform-shape [transform-fn shape]
  (set (map transform-fn shape)))

(def all-transformations [rotate-cw rotate-ccw (comp rotate-cw rotate-cw)
                          (comp flip-horiz rotate-cw) (comp flip-horiz rotate-ccw) (comp flip-horiz rotate-cw rotate-cw)
                          (comp flip-vert rotate-cw) (comp flip-vert rotate-ccw) (comp flip-vert rotate-cw rotate-cw)])

(defn print-shape [coords]
  (let [size 14
        empty-grid (vec (repeat size (vec (repeat size "."))))

        grid (reduce (fn [g [x y]]
                       (if (and (< -1 x size) (< -1 y size)) ;; Bounds check
                         (assoc-in g [y x] "#")
                         g))
                     empty-grid
                     coords)]

    (doseq [row grid]
      (println (str/join " " row)))))

(defn can-place? [region shape]
  (every? #(not (contains? region %)) shape))

(defn backtrack [region max-x max-y to-fit idx shapes]
  (if (= idx (count to-fit))
    true
    (let [a-gift (nth shapes (nth to-fit idx))
          all-t (conj (set (map #(transform-shape % a-gift) all-transformations)) a-gift)]

      (reduce (fn [acc-region shape]
                (if acc-region
                  (reduced acc-region)
                  (if (can-place? region shape)
                    (backtrack (set/union region shape) max-x max-y to-fit (inc idx) shapes)
                    acc-region)))
              false
              (apply set/union (map (fn [[x y]] (set (map #(transform-shape (partial offset-x x) (transform-shape (partial offset-y y) %)) all-t))) (for [x (range (inc (- max-x 3))) y (range (inc (- max-y 3)))] [x y])))))))

(def m-all-t
  (memoize
   (fn [g max-x max-y]
     (let [all-t (conj (set (map #(transform-shape % g) all-transformations)) g)]
       (apply set/union
              (map
               (fn [[x y]] (set (map #(transform-shape (partial offset-x x) (transform-shape (partial offset-y y) %)) all-t)))
               (for [x (range (inc (- max-x 3))) y (range (inc (- max-y 3)))] [x y])))))))

(defn generate-next-states [{:keys [gifts region quantities max-x max-y] :as current-state}]
  (let [[i q] (first (keep-indexed #(when-not (zero? %2) [%1 %2]) quantities))
        g (nth gifts i)
        only-valid (filter #(can-place? region %) (m-all-t g max-x max-y))]
    (map (fn [coll] {:gifts gifts :region (set/union region coll) :quantities (update quantities i dec) :max-x max-x :max-y max-y}) only-valid)))

(defn backtrack-2
  "Cleaner version of the same backtracking solution using idiomatic Clojure"
  [{:keys [gifts region quantities max-x max-y] :as current-state}]
  ;;  (do (println quantities))
  (if (= quantities [0 0 0 0 0 0])
    true
    (some backtrack-2 (generate-next-states current-state))))

(defn reduce-by-packing-4-by-4
  "Heuristic specific to day12 input - reducing problem space by packing pairs of gifts into 4x4 boxes"
  [{:keys [quantities max-x max-y] :as current-state}]
  (let [[q0 q1 q2 q3 q4 q5] quantities
        m1 (min q0 q4)
        m2 (min q1 q5)
        m3 (int (/ q3 2))
        total-4-by-4 (+ m1 m2 m3)
        total-per-row (int (/ max-y 4))
        rows-needed (if (zero? (mod total-4-by-4 total-per-row)) (/ total-4-by-4 total-per-row) (inc (int (/ total-4-by-4 total-per-row))))
        new-max-x (- max-x (* rows-needed 4))]
    (assoc current-state :max-x new-max-x :quantities [(- q0 m1) (- q1 m2) q2 (- q3 (* 2 m3)) (- q4 m1) (- q5 m2)])))

(defn is-feasable? [gifts {:keys [quantities max-x max-y]}]
  (let [gift-sizes (map count gifts)
        total-area (* max-x max-y)
        required-area (apply + (map #(apply * %) (partition 2 (interleave quantities gift-sizes))))]
    (> total-area required-area)))

(defn solve [{:keys [gifts regions]}]
  (let [valid (filter (partial is-feasable? gifts) regions)]
    (count (filter #(backtrack-2 (reduce-by-packing-4-by-4 (assoc % :gifts gifts :region #{}))) valid))))

(defn -main []
  (let [input (parse-input "resources/day12.txt")
;;         sample-region (second (:regions input))
;;         current-state {:gifts (:gifts input) :region #{} :quantities (:quantities sample-region) :max-x (:max-x sample-region) :max-y (:max-y sample-region)}
        ]
    (time
     (println "Part 1: "
              (solve input)))))
