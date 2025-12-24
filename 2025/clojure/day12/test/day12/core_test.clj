(ns day12.core-test
  (:require [clojure.test :refer :all]
            [day12.core :refer :all]))

(deftest test-day12
  (testing "Day 12 test"
    (let [input (parse-input "resources/day12.txt")]
      (is (= 5 (solve input))))))
