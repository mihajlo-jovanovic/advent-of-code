(ns day18.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))
        lumber-map (into {}
                         (mapcat (fn [[y line]]
                                   (map-indexed (fn [x c] {[x y] c}) line))
                                 (map-indexed vector lines)))]
    {:lumber-map lumber-map
     :size (count lines)}))

(defn change-single-acre [[[x y] c] {:keys [lumber-map size]}]
  (let [neighbors (for [a [-1 0 1] b [-1 0 1]
                        :let [new-x (+ x a) new-y (+ y b)]
                        :when (and (< -1 new-x size) (< -1 new-y size) (not (and (zero? a) (zero? b))))]
                    (get lumber-map [new-x new-y]))
        total-adj-acres-w-trees (count (filter #(= \| %) neighbors))
        total-adj-acres-w-lumberyards (count (filter #(= \# %) neighbors))
        new-c
        (case (str c)
          "." (if (>= total-adj-acres-w-trees 3) \| \.)
          "|" (if (>= total-adj-acres-w-lumberyards 3) \# \|)
          "#" (if (and (>= total-adj-acres-w-trees 1) (>= total-adj-acres-w-lumberyards 1)) \# \.))]
    [[x y] new-c]))

(defn change-entire-landscape [input]
  {:lumber-map
   (into {} (map #(change-single-acre % input) (:lumber-map input)))
   :size (:size input)})

(defn print-landscape [{:keys [lumber-map size]}]
  (let [empty-grid (vec (repeat size (vec (repeat size " "))))

        grid (reduce (fn [g [[x y] c]]
                       (assoc-in g [y x] c))
                     empty-grid
                     lumber-map)]

    (doseq [row grid]
      (println (str/join " " row)))))

(defn solve [input offset]
  (let [after-10-mins (last (take offset (iterate change-entire-landscape input)))
        total-acres-w-trees (count (filter #(= \| (val %)) (:lumber-map after-10-mins)))
        total-acres-w-lumberyards (count (filter #(= \# (val %)) (:lumber-map after-10-mins)))]
    (* total-acres-w-trees total-acres-w-lumberyards)))

(defn idx-of-first-duplicate [coll]
  (reduce (fn [{:keys [seen reverse-idx]} [idx x]]
            (if (contains? seen x)
              (reduced [(get reverse-idx x) idx])
              {:seen (conj seen x) :reverse-idx (assoc reverse-idx x idx)}))
          {:seen #{} :reverse-idx {}} (map-indexed vector coll)))

(defn p1 [input]
  (solve input 11))

(defn p2 [input]
  (let [goal-minutes 1000000000
        [loop-start-idx loop-end-idx] (idx-of-first-duplicate (iterate change-entire-landscape input))
        loop-len (- loop-end-idx loop-start-idx)
        offset (+ loop-start-idx (mod (- (inc goal-minutes) loop-start-idx) loop-len))]
    (solve input offset)))

(defn -main []
  (let [input (parse-input "resources/day18.txt")]
    (println "Part 2: " (time (p2 input)))))
