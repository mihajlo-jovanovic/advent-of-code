(ns day24.core-test
  (:require [clojure.test :refer :all]
            [day24.core :refer :all]))

(deftest test-part1
  (testing "part 1"
    (is (= 5216 (p1 (parse-input "resources/sample.txt"))))
    (is (= 10538 (p1 (parse-input "resources/day24.txt"))))))

(deftest test-part2
  (testing "part 2"
    (is (= 51 (p2 (parse-input "resources/sample.txt"))))
    (is (= 9174 (p2 (parse-input "resources/day24.txt"))))))
