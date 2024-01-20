(ns day17.core-test
  (:require [clojure.test :refer :all]
            [day17.core :refer :all]))

(deftest part-2-test
  (testing "Day 17 tests"
    (let [input (parse-input (slurp "resources/input-sample.txt"))
          input-full (parse-input (slurp "resources/day17.txt"))]
      (is (= 102 (part1 input)))
      (is (= 851 (part1 input-full)))
      (is (= 94 (part2 input)))
      (is (= 982 (part2 input-full))))))
