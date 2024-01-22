(ns day18.core-test
  (:require [clojure.test :refer :all]
            [day18.core :refer :all]))

(deftest test-solve
  (testing "Day 18 tests"
    (let [input-sample (slurp "resources/input-sample.txt")
          input (slurp "resources/day18.txt")]
      (is (= 62 (part1 input-sample)))
      (is (= 952408144115 (part2 input-sample)))
      (is (= 35401 (part1 input)))
      (is (= 48020869073824 (part2 input))))))
