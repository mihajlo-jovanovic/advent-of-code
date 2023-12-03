(ns day4.core-test
  (:require [clojure.test :refer :all]
            [day4.core :refer :all]))

(deftest day4-test
  (testing "day 4 test"
    (let [input (parse-input "resources/input.txt")
          input-test (parse-input "resources/input_test.txt")]
      (is (= 13 (p1 input-test)))
      (is (= 30 (p2 input-test)))
      (is (= 25651 (p1 input)))
      (is (= 19499881 (p2 input))))))