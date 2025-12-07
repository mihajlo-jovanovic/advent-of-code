(ns day7.core
  (:require [clojure.string :as str]))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))]
    (vec lines)))

(defn p2 [filepath]
  (let [grid (parse-input filepath)
        max-row (dec (count grid))
        start-col (.indexOf (first grid) "S")
        dfs (atom nil)]
    (reset! dfs (memoize (fn [row col]
                           (cond
                             (>= row max-row) 1
                             (= (get-in grid [row col]) \^) (+ (@dfs (inc row) (dec col))
                                                               (@dfs (inc row) (inc col)))
                             :else (@dfs (inc row) col)))))
    (@dfs 1 start-col)))
