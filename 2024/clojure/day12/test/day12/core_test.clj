(ns day12.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day12.core :refer [part1 part2]]))

(deftest day12-tests
  (testing "Day 12 - part 1"
    (is (= 140 (part1 "resources/sample.txt")))
    (is (= 772 (part1 "resources/sample2.txt")))
    (is (= 1930 (part1 "resources/sample3.txt")))
    (is (= 1450816 (part1 "resources/day12.txt")))))

(deftest day12-part2-tests
  (testing "Day 12 - part 2"
    (is (= 80 (part2 "resources/sample.txt")))
    (is (= 436 (part2 "resources/sample2.txt")))
    (is (= 236 (part2 "resources/sample4.txt")))
    (is (= 368 (part2 "resources/sample5.txt")))
    (is (= 1206 (part2 "resources/sample3.txt")))
    (is (= 865662 (part2 "resources/day12.txt")))))