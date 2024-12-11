(ns day11.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day11.core :refer [blink-seq m-count-stones]]))

(deftest counting-stones
  (testing "Day 11: Counting Stones"
    (let [stones '(1117 0 8 21078 2389032 142881 93 385)]
      (is (= '(1 2024 1 0 9 9 2021976) (blink-seq '(0 1 10 99 999))))
      (is (= 22 (count (last (take 7 (iterate blink-seq '(125 17)))))))
      (is (= 55312 (count (last (take 26 (iterate blink-seq '(125 17)))))))
      (is (= 224529 (count (last (take 26 (iterate blink-seq stones))))))
      (is (= 266820198587914 (reduce + (map #(m-count-stones % 75) stones)))))))
