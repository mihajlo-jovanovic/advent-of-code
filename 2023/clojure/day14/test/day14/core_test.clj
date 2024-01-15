(ns day14.core-test
  (:require [clojure.test :refer :all]
            [day14.core :refer :all]))

(deftest test-parse-input
  (testing "Parse input"
    (let [input (slurp "resources/input-sample.txt")]
      (is (= 35 (count (parse-input input))))
      (is (contains? (parse-input input) {:x 0, :y 9, :value \#}))
      (is (contains? (parse-input input) {:x 0, :y 0, :value \O})))))


(deftest test-day14
  (testing "Day 14 tests"
    (let [input (convert-input (parse-input (slurp "resources/input-sample.txt")))
          ;; input-full (convert-input (parse-input (slurp "resources/day14.txt")))
          ]
      (is (= 136 (reduce + (map second (:round (tilt-north input))))))
      ;; (is (= 107951 (reduce + (map second (:round (tilt-north input-full))))))
      ;; (is (= 95736 (part2 input-full)))
      (is (= 64 (part2 input))))))
