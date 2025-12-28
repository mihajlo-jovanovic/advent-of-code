(ns day13.core-test
  (:require [clojure.test :refer :all]
            [day13.core :refer :all]))

(deftest test-day13
  (testing "Day 13 Tests"
    (let [input-sample-p1 (parse-input "resources/sample.txt")
          input-sample-p2 (parse-input "resources/sample2.txt")
          input-full (parse-input "resources/day13.txt")]
      (is (= "7,3" (p1 input-sample-p1)))
      (is (= "58,93" (p1 input-full)))
      (is (= [6 4] (p2-reduce input-sample-p2)))
      (is (= [91 72] (p2-reduce input-full))))))
