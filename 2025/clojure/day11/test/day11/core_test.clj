(ns day11.core-test
  (:require [clojure.test :refer :all]
            [day11.core :refer :all]))

(deftest test-p2
  (testing "Day 11 Part 2"
    (is (= 2 (p2 "resources/sample-p2.txt")))
    (is (= 370500293582760 (p2 "resources/day11.txt")))))
