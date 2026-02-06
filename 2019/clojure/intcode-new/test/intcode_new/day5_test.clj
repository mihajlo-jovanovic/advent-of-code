(ns intcode-new.day5-test
  (:require [clojure.test :refer :all]
            [intcode-new.intcode :as ic]
            [intcode-new.day5 :as d5]))

(deftest test-day5
  (testing "Day 5"
    (is (= 5346030 (d5/solve-day5-part1 (ic/parse-input "resources/day5.txt"))))))
