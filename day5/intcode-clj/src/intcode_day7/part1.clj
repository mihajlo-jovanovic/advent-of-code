(ns intcode-day7.part1
  (:require [intcode-clj.core :as intcode] ))

(defn di [s ph]
  (let [a (intcode/run s #(if (= 0 %) (str (first ph)) "0"))
        b (intcode/run s #(if (= 0 %) (str (second ph)) (str (last a))))
        c (intcode/run s #(if (= 0 %) (str (nth ph 2)) (str (last b))))
        d (intcode/run s #(if (= 0 %) (str (nth ph 3)) (str (last c))))]
    (last (intcode/run s #(if (= 0 %) (str (last ph)) (str (last d))))))
)

(defn permutations [s]
  (lazy-seq
   (if (seq (rest s))
     (apply concat (for [x s]
                     (map #(cons x %) (permutations (remove #{x} s)))))
     [s])))

(defn part1
  []
  (apply max (map (partial di (slurp "resources/day7-input.dat")) (permutations '(0 1 2 3 4)))))
