(ns day9.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day9.core :refer [part1 part2]]))

(deftest day9-tests
  (testing "Day 9: disk maps"
    (is (= 60 (part1 "resources/sample-small.txt")))
    (is (= 1928 (part1 "resources/sample.txt")))
    (is (= 6401092019345 (part1 "resources/input.txt")))
    (is (= 132 (part2 "resources/sample-small.txt")))
    (is (= 2858 (part2 "resources/sample.txt")))))
