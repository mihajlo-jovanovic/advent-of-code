(ns day4.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day4.core :refer [parse-grid-map p1 p2]]))

(deftest test-day4
  (testing "Day 4 - looking for XMAS"
    (let [grid-sm (parse-grid-map (slurp "resources/sample.txt"))
          grid-lg (parse-grid-map (slurp "resources/input.txt"))]
      (is (= 18 (p1 grid-sm)))
      (is (= 9 (p2 grid-sm)))
      (is (= 2562 (p1 grid-lg)))
      (is (= 1902 (p2 grid-lg))))))
