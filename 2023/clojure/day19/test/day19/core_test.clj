(ns day19.core-test
  (:require [clojure.test :refer :all]
            [day19.core :refer :all]))

(deftest day19.core-test
  (testing "Day 19 tests"
    (let [test-input (slurp "resources/day19-test.txt")
          parts (clojure.string/split test-input #"\n\n")
          workflows (parse-input (first parts))
          ratings (parse-ratings (second parts))]
      (is (= 19114 (p1 workflows ratings)))
      (is (= 167409079868000 (p2 workflows))))))