(ns day23.core-test
  (:require [clojure.test :refer :all]
            [day23.core :refer :all]))

(deftest test-manhattan-distance
  (testing "manhattan distance"
    (is (= 0 (manhattan-distance [0 0 0] [0 0 0])))
    (is (= 1 (manhattan-distance [0 0 0] [1 0 0])))
    (is (= 4 (manhattan-distance [0 0 0] [4 0 0])))
    (is (= 2 (manhattan-distance [0 0 0] [0 2 0])))
    (is (= 5 (manhattan-distance [0 0 0] [0 5 0])))
    (is (= 3 (manhattan-distance [0 0 0] [0 0 3])))
    (is (= 3 (manhattan-distance [0 0 0] [1 1 1])))
    (is (= 4 (manhattan-distance [0 0 0] [1 1 2])))
    (is (= 5 (manhattan-distance [0 0 0] [1 3 1])))))

(deftest test-p1
  (testing "part 1"
    (is (= 7 (p1 (parse-input "resources/sample.txt"))))
    (is (= 889 (p1 (parse-input "resources/day23.txt"))))))

(deftest test-p2
  (testing "part 1"
    (is (= 36 (p2 (parse-input "resources/sample2.txt"))))
    (is (= 160646364 (p2 (parse-input "resources/day23.txt"))))))
