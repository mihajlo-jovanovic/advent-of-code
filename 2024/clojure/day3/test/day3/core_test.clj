(ns day3.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day3.core :refer [p1 p2]]))

(deftest process-multiply-instructions
  (testing "Day 3 - Process multiply instructions"
    (let [sample "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
          input (slurp "resources/input.txt")]
      (is (= 161 (p1 sample)))
      (is (= 48 (p2 sample)))
      (is (= 188741603 (p1 input)))
      (is (= 67269798 (p2 input))))))
