(ns day8-clj.core
  (:require [clojure.string :as str]))

(defn combine-layers
  [x y]
  (letfn [(combine' [x y] (if (= 2 x) y x))]
    (let [pairs (partition-all 2 (interleave x y))]
      (map #(reduce combine' %) pairs))))

(defn -main
  "puzzle solution"
  [in]
  (let [input-seq (map #(Integer/parseInt %) (str/split in #""))]
    (partition-all 25 (reduce combine-layers (partition-all 150 input-seq)))))
