(ns day25.core-test
  (:require [clojure.test :refer :all]
            [day25.core :refer :all]))

(deftest test-part1
  (testing "Part 1"
    (is (= 359 (p1 (parse-input "resources/day25.txt"))))))
