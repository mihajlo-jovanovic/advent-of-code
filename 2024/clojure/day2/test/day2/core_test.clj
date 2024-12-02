(ns day2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day2.core :refer [parse-input p1 p2]]))

(deftest safe-reports
  (testing "Day 2 - Safe Reports"
    (let [sample (parse-input "resources/sample.txt")
          input (parse-input "resources/input.txt")]
      (is (= 2 (p1 sample)))
      (is (= 4 (p2 sample)))
      (is (= 282 (p1 input)))
      (is (= 349 (p2 input))))))