(ns day18.core-test
  (:require [clojure.test :refer :all]
            [day18.core :refer :all]))

(deftest test-both-parts
  (testing "Day 18 p1 and p2"
    (let [input-sample (parse-input "resources/sample.txt")
          input-full (parse-input "resources/day18.txt")]
      (is (= 1147 (p1 input-sample)))
      (is (= 531417 (p1 input-full)))
      (is (= 205296 (p2 input-full))))))
