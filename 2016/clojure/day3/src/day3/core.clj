(ns day3.core
  (:require
   [clojure.string :as str]))

(def input (map #(str/split % #" ") (str/split-lines (slurp "resources/input.txt"))))

(defn parse-line [l] (map #(Integer/parseInt %) (filter #(seq %) l)))

(def nums (map parse-line input))

(defn valid-triangle? [coll] (let [[a b c] coll] (and (> (+ a b) c) (> (+ a c) b) (> (+ b c) a))))

;; part1
(count (filter valid-triangle? nums))

;; part 2
;; (count (filter valid-triangle? (partition 3 (concat (map first nums) (map second nums) (map #(nth % 2) nums)))))