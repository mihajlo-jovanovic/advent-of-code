(ns day13.core-test
  (:require [clojure.test :refer :all]
            [day13.core :refer :all]))

(deftest test-part1
  (testing "Day 13 tests"
    (let [input (parse-input (slurp "resources/input-sample.txt"))
          input-full (parse-input (slurp "resources/day13.txt"))]
      (is (= 405 (reduce + (map part1 input))))
      (is (= 28651 (reduce + (map part1 input-full))))
      (is (= 400 (reduce + (map part2 input))))
      (is (= 25450 (reduce + (map part2 input-full)))))))
