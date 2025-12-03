(ns day3.core
  (:require [clojure.string :as s]))

(defn parse-input [filepath]
  (let [to-digits (fn [s] (mapv #(Character/digit % 10) s))]
    (->> (slurp filepath)
         (s/split-lines)
         (map to-digits))))

(defn find-largest-joltage [v]
  (let [first-battery (apply max (drop-last v))
        idx (.indexOf v first-battery)
        second-battery (apply max (drop (inc idx) v))]
    (+ (* first-battery 10) second-battery)))

(defn find-largest-joltage-p2 [v i offset]
  (let [v2 (into [] (drop offset v))
        how-many-to-ignore-back (inc (* 2 (- 5 i)))
        v3 (into [] (drop-last how-many-to-ignore-back v2))
        first-battery (apply max v3)
        idx (.indexOf v2 first-battery)
        second-battery (apply max (drop (inc idx) (into [] (drop-last (* 2 (- 5 i)) v2))))
        offset-new (.indexOf (into [] (drop (inc idx) v2)) second-battery)]
    [(+ (* first-battery 10) second-battery) (+ offset (inc (+ (inc idx) offset-new)))]))

(defn p2 [input]
  (let [joltage-output (fn [digits]
                         (reduce
                          #(let [acc (find-largest-joltage-p2 digits %2 (second %1))]
                             [(str (first %1) (str (first acc))) (second acc)])
                          ["" 0]  ;; first: digits, second: offset
                          (range 0 6)))]
    (->> input
         (map joltage-output)
         (map first)
         (map Long/parseLong)
         (reduce +))))
