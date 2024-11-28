(ns day8.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day8.core :refer [parse-input parse-tree part1 part2]]))

(deftest my-solution-to-day8
  (testing "License file parsing"
    (let [input-test (parse-input "resources/sample.txt")
          input-full (parse-input "resources/day8.txt")
          tree-test (first (parse-tree input-test))
          tree-full (first (parse-tree input-full))]
      (is (= 138 (part1 "resources/sample.txt")))
      (is (= 40701 (part1 "resources/day8.txt")))
      (is (= 66 (part2 tree-test)))
      (is (= 21399 (part2 tree-full))))))