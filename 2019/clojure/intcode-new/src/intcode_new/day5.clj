(ns intcode-new.day5
  (:require [intcode-new.intcode :as intcode]))

(defn solve-part1 [input]
  (let [final-state (intcode/run (intcode/load-program input))]
    (last (:out final-state))))
