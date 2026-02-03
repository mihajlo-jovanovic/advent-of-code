(ns intcode-new.core-test
  (:require [clojure.test :refer :all]
            [intcode-new.core :refer :all]))

(deftest test-part
  (testing "Part 1"
    (is (= [2 0 0 0 99] (:program (run (load-program [1,0,0,0,99])))))
    (is (= [2 3 0 6 99] (:program (run (load-program [2,3,0,3,99])))))
    (is (= [2,4,4,5,99,9801] (:program (run (load-program [2,4,4,5,99,0])))))
    (is (= [30,1,1,4,2,5,6,0,99] (:program (run (load-program [1,1,1,4,99,5,6,0,99])))))
    (is (= [3500,9,10,70,2,3,11,0,99,30,40,50] (:program (run (load-program [1,9,10,3,2,3,11,0,99,30,40,50])))))
    (is (= 2782414 (p1 (parse-input "resources/day2.txt") 12 2)))
    (is (= 9820 (p2 (parse-input "resources/day2.txt"))))))

(deftest test-day5
  (testing "Day 5"
    (is (= 5346030 (day5-p1 (parse-input "resources/day5.txt"))))))
