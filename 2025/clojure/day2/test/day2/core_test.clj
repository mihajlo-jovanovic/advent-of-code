(ns day2.core-test
  (:require [clojure.test :refer :all]
            [day2.core :refer :all]))

(deftest test-day2
  (testing "Day 2 tests"
    (let [sample-ranges (parse-input "resources/sample.txt")
          id-ranges (parse-input "resources/day2.txt")]
      (is (= 1227775554 (p1 sample-ranges)))
      (is (= 29818212493 (p1 id-ranges)))
      (is (= 4174379265 (p2 sample-ranges)))
      (is (= 37432260594 (p2 id-ranges))))))
