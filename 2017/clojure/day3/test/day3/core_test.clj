(ns day3.core-test
  (:require [clojure.test :refer :all]
            [day3.core :refer :all]))

(deftest p1-test
  (testing "part 1"
    (is (= 326 (p1)))))

(deftest p2-test
  (testing "part 2"
    (is (= 363010 (p2)))))