(ns day22.core
  (:require [clojure.string :as string]))

(defn parse-input [input]
  (let [parse-coords (fn [s] (into [] (map #(Integer/parseInt %) (string/split s #","))))]
    (->> input
         string/split-lines
         (map #(string/split % #"~"))
         (map #(vector (parse-coords (first %)) (parse-coords (second %)))))))

(defn overlap?
  [[[x1 y1 _] [x2 y2 _]] [[x3 y3 _] [x4 y4 _]]]
  (let [x (max (min x1 x2) (min x3 x4))
        y (max (min y1 y2) (min y3 y4))
        x' (min (max x1 x2) (max x3 x4))
        y' (min (max y1 y2) (max y3 y4))]
    (and (<= x x') (<= y y'))))

(defn lower-brick [brick other-bricks]
  (let [can-move? (fn [brick z]
                    (not (some #(and (overlap? brick %)
                                     (<= (nth (first %) 2) z
                                         (nth (last %) 2)))
                               other-bricks)))]
    (loop [z1 (nth (first brick) 2)
           z2 (nth (second brick) 2)]
      (if (and (> z1 1) (can-move? brick (dec z1)))
        (recur (dec z1) (dec z2))
        [(update (first brick) 2 (constantly z1))
         (update (second brick) 2 (constantly z2))]))))

(defn lower-all-bricks [bricks]
  (reduce (fn [acc brick]
            (conj acc (lower-brick brick acc)))
          []
          bricks))

(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn p1 [bricks]
  (let [sorted-bricks (sort-by (fn [brick] (nth (first brick) 2)) bricks)
        after-fall (lower-all-bricks sorted-bricks)]
    (count (filter
            (fn [i]
              (let [take-out-brick (vec-remove i after-fall)]
                (= take-out-brick (lower-all-bricks take-out-brick))))
            (range 0 (count after-fall))))))

(defn p2 [bricks]
  (let [count-fallen (fn [before after] (reduce + (pmap (fn [[x y]] (if (= x y) 0 1)) (partition 2 (interleave before after)))))
        sorted-bricks (sort-by (fn [brick] (nth (first brick) 2)) bricks)
        after-fall (lower-all-bricks sorted-bricks)]
    (reduce + (pmap
               (fn [i]
                 (let [take-out-brick (vec-remove i after-fall)]
                   (count-fallen take-out-brick (lower-all-bricks take-out-brick))))
               (range 0 (count after-fall))))))

;; (defn p1-2 []
;;   (let [input (parse-input (slurp "resources/day22.txt"))
;;         bricks (map (fn [{:keys [start end]}] (vector (vector (:x start) (:y start) (:z start)) (vector (:x end) (:y end) (:z end)))) input)
;;         sorted-bricks (sort-by (fn [[[_ _ z1] [_ _ _]]] z1) bricks)
;;         after-fall (lower-all-bricks sorted-bricks)
;;         m (group-by (fn [[[_ _ z1] [_ _ z2]]] (min z1 z2)) after-fall)]
;;     (count (filter (fn [[[x1 y1 z1] [x2 y2 z2]]]
;;                      (let [all-bricks-at-level (get m (min z1 z2))
;;                            minus-this-one (filter #(not= % [[x1 y1 z1] [x2 y2 z2]]) all-bricks-at-level)
;;                            all-bricks-next-level (get m (inc (max z1 z2)))]
;;                        (cond
;;                          (empty? all-bricks-next-level) true
;;                          (and (= 1 (count all-bricks-at-level)) (seq all-bricks-next-level)) false
;;                          :else (every? #(or (not (overlap?  [[x1 y1 z1] [x2 y2 z2]] %)) (some (fn [b] (supports? [b %])) minus-this-one)) all-bricks-next-level))))
;;                    after-fall))))