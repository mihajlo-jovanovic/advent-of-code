(ns day8.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day8.core :refer [part1 part2]]))

(deftest count-antinodes
  (testing "Day 8 - counting anthenas and antinodes"
    (is (= 14 (part1 "resources/sample.txt")))
    (is (= 34 (part2 "resources/sample.txt")))
    (is (= 9 (part2 "resources/sample2.txt")))
    (is (= 323 (part1 "resources/input.txt")))
    (is (= 1077 (part2 "resources/input.txt")))))