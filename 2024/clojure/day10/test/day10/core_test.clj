(ns day10.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day10.core :refer [parse-grid part1 part2]]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 (part1 (parse-grid "resources/sample.txt"))))
    (is (= 2 (part1 (parse-grid "resources/sample2.txt"))))
    (is (= 4 (part1 (parse-grid "resources/sample3.txt"))))
    (is (= 3 (part1 (parse-grid "resources/sample4.txt"))))
    (is (= 36 (part1 (parse-grid "resources/sample5.txt"))))
    (is (= 482 (part1 (parse-grid "resources/input.txt"))))))

(deftest a-test-p2
  (testing "FIXME, I fail."
    (is (= 16 (part2 (parse-grid "resources/sample.txt"))))
    (is (= 2 (part2 (parse-grid "resources/sample2.txt"))))
    (is (= 13 (part2 (parse-grid "resources/sample3.txt"))))
    (is (= 3 (part2 (parse-grid "resources/sample4.txt"))))
    (is (= 81 (part2 (parse-grid "resources/sample5.txt"))))
    (is (= 3 (part2 (parse-grid "resources/sample6.txt"))))
    (is (= 13 (part2 (parse-grid "resources/sample7.txt"))))
    (is (= 227 (part2 (parse-grid "resources/sample8.txt"))))
    (is (= 1094 (part2 (parse-grid "resources/input.txt"))))))