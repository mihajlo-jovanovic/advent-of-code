(ns day22.core-test
  (:require [clojure.test :refer :all]
            [day22.core :refer :all]))

(deftest day22-tests
  (testing "Day 22"
    (let [input (parse-input (slurp "resources/input.txt"))
          input-full (parse-input (slurp "resources/day22.txt"))]
      (is (= 5 (p1 input)))
      (is (= 7 (p2 input)))
      (is (= 509 (p1 input-full)))
      (is (= 102770 (p2 input-full))))))
