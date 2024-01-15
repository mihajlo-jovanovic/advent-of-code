(ns day15.core-test
  (:require [clojure.test :refer :all]
            [day15.core :refer :all]))

(deftest part2-tests
  (testing "Day 15 tests"
    (let [input-sample (slurp "resources/input-sample.txt")
          ;; input (slurp "resources/day15.txt")
          ]
      (is (= 1320 (reduce + (map hash (clojure.string/split input-sample #",")))))
      ;; (is (= 212449 (part2 input)))
      (is (= 145 (part2 input-sample)))
      ;; (is (= 212449 (part2 input)))
      )))
