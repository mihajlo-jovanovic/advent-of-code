(ns intcode.amplification-circuit
  (:require [intcode.core :refer [run-new-async]]
            [clojure.core.async :as a]
            [clojure.math.combinatorics :as combo]))

(defn intcode-adapter [state x]
  (let [in (a/chan) out (a/chan)
        st (run-new-async {:memory state} in out)]
    (a/put! in x)
    {:state st :in in :out out}))

(defn day7-part1 [program settings]
  (let [[a b c d e] settings 
        a (intcode-adapter program a)
        b (intcode-adapter program b)
        c (intcode-adapter program c)
        d (intcode-adapter program d)
        e (intcode-adapter program e)]
    (doseq [[x y] (partition 2 1 [a b c d e a])]
      (a/pipe (:out x) (:in y)))
    (a/put! (:in a) 0)
    (a/alts!! [(:state e) (a/timeout 5000)])
    (a/poll! (:in a))))

(defn run [program]
  (->> (combo/permutations [5 6 7 8 9])
       (map #(day7-part1 program %))
       (apply max))
  )
