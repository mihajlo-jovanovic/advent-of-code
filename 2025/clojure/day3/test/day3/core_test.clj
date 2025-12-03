(ns day3.core-test
  (:require [clojure.test :refer :all]
            [day3.core :refer :all]))

(deftest a-test-day3
  (testing "Day 3 Tests"
    (is (= 3121910778619 (p2 (parse-input "resources/sample.txt"))))
    (is (= 172681562473501 (p2 (parse-input "resources/day3.txt"))))))
