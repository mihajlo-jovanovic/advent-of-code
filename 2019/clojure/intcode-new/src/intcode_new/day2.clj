(ns intcode-new.day2
  (:require [intcode-new.intcode :as intcode]))

(defn set-noun-verb [{:keys [mem] :as st} noun verb]
  (-> st
      (assoc-in [:mem 1] noun)
      (assoc-in [:mem 2] verb)))

(defn solve-day2-part1 [input noun verb]
  (first (:mem  (intcode/run (set-noun-verb (intcode/load-program input) noun verb)))))

(defn solve-day2-part2 [input]
  (let [[n v] (first (filter #(= 19690720 (solve-day2-part1 input (first %) (second %))) (for [noun (range 100) verb (range 100)] [noun verb])))]
    (+ (* 100 n) v)))
