(ns day6.core-test
  (:require [clojure.test :refer :all]
            [day6.core :refer :all]))

(deftest test-day6
  (testing "Day 6 tests"
    (let [input-test (parse-input "resources/sample.txt")
          input-full (parse-input "resources/day6.txt")]
      (is (= 4277556 (p1 input-test)))
      (is (= 7326876294741 (p1 input-full)))
      (is (= 3263827 (p2 input-test)))
      (is (= 10756006415204 (p2 input-full))))))
