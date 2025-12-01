(ns day1.core-test
  (:require [clojure.test :refer :all]
            [day1.core :refer :all]))

(deftest p1-test
  (testing "Count of times dial points at 0 at end"
    (is (= 3 (p1 "resources/sample.txt")))
    (is (= 1052 (p1 "resources/day1.txt")))))

(deftest p2-test
  (testing "Count of times dial points at 0 any time"
    (is (= 6 (first (p2 "resources/sample.txt"))))
    (is (= 6295 (first (p2 "resources/day1.txt"))))))
