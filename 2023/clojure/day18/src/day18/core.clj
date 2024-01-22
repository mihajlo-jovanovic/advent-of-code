(ns day18.core
  (:require [clojure.string :as string]
            [clojure.core.matrix :as matrix]))

(defn parse-input-p1 [s]
  (map (fn [line] (let [parts (string/split line #" ")]
                    {:direction (first parts)
                     :distance (Integer/parseInt (second parts))})) (string/split-lines s)))

(defn parse-input-p2 [s]
  (let [remove-parentheses (fn [s]
                             (let [pattern #"^\((.*)\)$"
                                   match (re-find pattern s)]
                               (if match
                                 (second match)
                                 s)))]
    (map (fn [line] (let [parts (string/split line #" ")
                          color (remove-parentheses (last parts))]
                      {:direction (case (.charAt color 6) \0 "R" \1 "D" \2 "L" \3 "U")
                       :distance (Long/parseLong (subs color 1 6) 16)})) (string/split-lines s))))

(defn shoelace [coords]
  (long (abs (/ (reduce + (map matrix/det (partition 2 1 coords))) 2))))

(defn solve
  [input]
  (let [area (shoelace (reductions (fn [[x y] {:keys [direction distance]}]
                                     (let [directions  {"R" [1 0]
                                                        "L" [-1 0]
                                                        "U" [0 1]
                                                        "D" [0 -1]}
                                           delta (directions direction)
                                           new-x (+ x (* distance (first delta)))
                                           new-y (+ y (* distance (second delta)))]
                                       [new-x new-y])) [0 0] input))
        b (reduce + (map :distance input))
        i  (inc (- area (/ b 2)))]   ;; Pick's Theorem
    (+ b i)))

(defn part1 [input]
  (solve (parse-input-p1 input)))

(defn part2 [input]
  (solve (parse-input-p2 input)))

