(ns day5.core-test
  (:require [clojure.test :refer :all]
            [day5.core :refer :all]))

(deftest test-day5
  (testing "Day 5: Cafeteria"
    (let [input-sample (parse-input "resources/input.txt")
          input-full (parse-input "resources/day5.txt")]
      (is (= 3 (p1 input-sample)))
      (is (= 664 (p1 input-full)))
      (is (= 14 (p2 (:ranges input-sample))))
      (is (= 350780324308385 (p2 (:ranges input-full)))))))
