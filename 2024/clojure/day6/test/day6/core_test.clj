(ns day6.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day6.core :refer [part1 part2]]))

(deftest grid-movement-test
  (testing "Day 6: 2D Grid Movements"
    (is (= 41 (part1 "resources/sample.txt")))
    (is (= 5564 (part1 "resources/input.txt")))
    (is (= 6 (part2 "resources/sample.txt")))))
