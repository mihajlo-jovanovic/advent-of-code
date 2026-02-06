(ns intcode-new.day2
  (:require [intcode-new.intcode :as intcode]))

(defn set-noun-verb [{:keys [mem] :as st} noun verb]
  (-> st
      (assoc-in [:mem 1] noun)
      (assoc-in [:mem 2] verb)))

(defn solve-part1 [input noun verb]
  (first (:mem  (intcode/run (set-noun-verb (intcode/load-program input) noun verb)))))

(defn solve-part2 [input]
  (->> (for [noun (range 100)
             verb (range 100)
             :when (= 19690720 (solve-part1 input noun verb))]
         (+ (* 100 noun) verb))
       first))
