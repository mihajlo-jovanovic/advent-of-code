(ns intcode-new.day2-test
  (:require [clojure.test :refer :all]
            [intcode-new.intcode :as ic]
            [intcode-new.day2 :as d2]))

(deftest test-day2
  (testing "Day 2"
    (is (= 2782414 (d2/solve-part1 (ic/parse-input "resources/day2.txt") 12 2)))
    (is (= 9820 (d2/solve-part2 (ic/parse-input "resources/day2.txt"))))))
