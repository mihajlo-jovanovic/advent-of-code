(ns day7.core-test
  (:require [clojure.test :refer :all]
            [day7.core :refer :all]))

(deftest day7-test
  (testing "Day 7 test"
    (let [test-input (parse-input (slurp "resources/day7-test.txt"))
          input (parse-input (slurp "resources/day7.txt"))]
      (is (= 6440 (p1 test-input)))
      (is (= 5905 (p2 test-input)))
      (is (= 250370104 (p1 input)))
      (is (= 251735672 (p2 input))))))
