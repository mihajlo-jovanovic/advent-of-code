(ns day5.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day5.core :refer [parse-input part1 part2]]))

(deftest page-number-ordering
  (testing "Day 5: Print Queue"
    (let [sample-filename "resources/sample.txt"
          input-filename "resources/input.txt"
          sample-input (parse-input sample-filename)
          full-input (parse-input input-filename)]
      (is (= 143 (part1 (:rules sample-input) (:updates sample-input)))
          (is (= 123 (part2 (:rules sample-input) (:updates sample-input)))))
      (is (= 6041 (part1 (:rules full-input) (:updates full-input))))
      (is (= 4884 (part2 (:rules full-input) (:updates full-input)))))))