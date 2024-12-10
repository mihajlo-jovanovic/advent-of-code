(ns day10.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day10.core :refer [parse-grid part1]]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 (part1 (parse-grid "resources/sample.txt"))))
    (is (= 2 (part1 (parse-grid "resources/sample2.txt"))))
    (is (= 4 (part1 (parse-grid "resources/sample3.txt"))))
    (is (= 3 (part1 (parse-grid "resources/sample4.txt"))))
    (is (= 36 (part1 (parse-grid "resources/sample5.txt"))))
    (is (= 482 (part1 (parse-grid "resources/input.txt"))))))
