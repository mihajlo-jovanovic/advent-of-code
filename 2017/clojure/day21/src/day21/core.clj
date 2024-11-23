(ns day21.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-image [s separator]
  (let [lines (str/split s (re-pattern separator))
        size (count lines)
        coords (into #{} (for [[y line] (map-indexed vector lines)
                               [x char] (map-indexed vector line)
                               :when (= \# char)]
                           [x y]))]
    {:grid coords :size size}))

(defn parse-input [filename]
  (->> filename
       slurp
       str/split-lines
       (map (fn [line]
              (let [[in out] (str/split line #" => ")]
                {:in (parse-image in "/")
                 :out (parse-image out "/")})))))

;; rotation and flipping logic
(defn rotate-90-cw [[x y] sz]
  [(- sz y) x])

(defn flip-x [[x y] sz]
  [x (- sz y)])

(defn flip-y [[x y] sz]
  [(- sz x) y])

(defn all-rotations [image size]
  [image
   (set (map #(rotate-90-cw % size) image))
   (set (map (fn [x] (rotate-90-cw x size)) (set (map #(rotate-90-cw % size) image))))
   (set (map (fn [x] (rotate-90-cw x size)) (set (map (fn [x] (rotate-90-cw x size)) (set (map #(rotate-90-cw % size) image))))))])

(defn all-possible-transformations [image max-sz]
  (let [flipped-x (set (map #(flip-x % max-sz) image))
        flipped-y (set (map #(flip-y % max-sz) image))]
    (distinct (concat (all-rotations image max-sz)
                      (all-rotations flipped-x max-sz)
                      (all-rotations flipped-y max-sz)))))

(defn break-up
  "Break up the image into sub-images"
  [{:keys [grid size]}]
  (let [sub-size (if (even? size) 2 3)
        sub-images (for [i (range 0 (/ size sub-size))
                         j (range 0 (/ size sub-size))]
                     (let [sub-grid (into #{}
                                          (for [[x y] grid
                                                :when (and (< (dec (* i sub-size)) x (* (inc i) sub-size))
                                                           (< (dec (* j sub-size)) y (* (inc j) sub-size)))]
                                            [(- x (* i sub-size)) (- y (* j sub-size))]))]
                       {:grid sub-grid :size sub-size}))]
    sub-images))

(defn combine
  "Combine the sub-images into a single image"
  [images size]
  (let [cnt (count images)
        grid-sz (int (Math/sqrt cnt))
        offset (fn [[x y] size i j]  [(+ (* i size) x) (+ (* j size) y)])
        grid-new (for [i (range 0 grid-sz)
                       j (range 0 grid-sz)]
                   (map #(offset % size i j) (nth images (+ (* i grid-sz) j))))]
    {:grid (into #{} (apply concat grid-new)) :size (* grid-sz size)}))

(def rules (parse-input "resources/input.txt"))

(defn process-rule
  "Process a single rule (by returning the matching output image)"
  [{:keys [grid size]}]
  (let [applicable-rules (filter #(= size (:size (:in %))) rules)
        all-p (set (all-possible-transformations grid (dec size)))]
    (:out (first (filter #(contains? all-p (:grid (:in %))) applicable-rules)))))

;; part 1
(defn do-one-iteration [image]
  (let [sub-images (break-up image)
        new-images (map #(process-rule %) sub-images)]
    (combine (map :grid new-images) (:size (first new-images)))))

;; hack for part 2
(defn solve-for-9 [image]
  (let [iterations 9
        result (reduce (fn [acc _] (do-one-iteration acc)) image (range 0 iterations))]
    result))

(def solve-for-9-memo (memoize solve-for-9))

(def coords #{[2 2] [1 0] [0 2] [2 1] [1 2]})
(def start {:grid coords :size 3})

(defn -main []
  (let [result-after-9 (solve-for-9-memo start)
        sub-images (break-up result-after-9)]
    (println "Part 1: " (count (:grid (last (take 6 (iterate do-one-iteration start))))))
    (time (println "Part 2: " (reduce + (map #(count (:grid (solve-for-9-memo %))) sub-images))))))