(ns day11.core
  (:require
   [clojure.string :as str]))

(def origin [0 0 0])

(def mymap {"n" [0 1 -1],
            "s" [0 -1 1],
            "ne" [1 0 -1],
            "nw" [-1 1 0],
            "se" [1 -1 0],
            "sw" [-1 0 1]})

(defn add [[a b c] [d e f]] [(+ a d) (+ b e) (+ c f)])

(defn distance [a b] (apply max (map abs (add a b))))

(defn p1 [input]
  (distance (reduce add '(0 0 0) (map #(get mymap %) (str/split input #",")))  origin))

(defn p2 [input]
  (let [all-positions (reductions add origin (map #(get mymap %) (str/split input #",")))]
    (apply max (map #(distance % origin) all-positions))))