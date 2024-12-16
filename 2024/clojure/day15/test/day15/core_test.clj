(ns day15.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day15.core :refer [part1]]))

(deftest day15-part1
  (testing "Day 15: Lanternfish - Part 1"
    (is (= 2028 (part1 "resources/sample-sm.txt")))
    (is (= 10092 (part1 "resources/sample.txt")))
    (is (= 1505963 (part1 "resources/input.txt")))))
