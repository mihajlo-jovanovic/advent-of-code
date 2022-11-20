(ns intcode-day7.part1
  (:require [intcode.core :as intcode] ))

(defn chain-amps [s [f sec t m l]]
  (let [in #(if (= 0 %3) (str %) (str %2))
        a (intcode/run s (partial in f 0))
        b (intcode/run s (partial in sec (last a)))
        c (intcode/run s (partial in t (last b)))
        d (intcode/run s (partial in m (last c)))]
    (last (intcode/run s (partial in l (last d)))))
)

(defn permutations [s]
  (lazy-seq
   (if (seq (rest s))
     (apply concat (for [x s]
                     (map #(cons x %) (permutations (remove #{x} s)))))
     [s])))

(defn part1
  []
  (apply max (map (partial chain-amps (slurp "resources/day7-input.dat")) (permutations '(0 1 2 3 4)))))
