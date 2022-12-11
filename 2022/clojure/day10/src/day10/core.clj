(ns day10.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(defn get-input [in]
  (->> in io/resource slurp))

(defn run-vm-cycle [state]
  (let [f (first (:memory state))
        r (rest (:memory state))
        ln (str/split f #" ")
        op (first ln)]
    (case op
      "noop" {:memory r :x (:x state) :counter 1}
      "addx" (let [v (Integer/parseInt (second ln))]
               (if (> (:counter state) 0)
                 {:memory (:memory state) :x (:x state) :counter (dec (:counter state))}
                 {:memory r :x (+ (:x state) v) :counter 1})))))

(defn -main
  [& _]
  (let [input (-> (get-input "day10.txt")
                  str/split-lines)
        state {:memory input :x 1 :counter 1}
        significant-cycles (->> (range 0 6)
                                (map #(* % 40))
                                (map #(+ 20 %)))
        vals (map :x (take 220 (iterate run-vm-cycle state)))]
    (println "Part 1 solution: " (reduce + 0
                                         (map #(* (nth vals (dec %)) %) significant-cycles)))))