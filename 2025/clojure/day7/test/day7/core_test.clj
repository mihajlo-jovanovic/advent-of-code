(ns day7.core-test
  (:require [clojure.test :refer :all]
            [day7.core :refer :all]))

(deftest p2-sample-test
  (is (= 40 (p2 "resources/sample.txt"))))

(deftest p2-day7-test
  (is (= 47274292756692 (p2 "resources/day7.txt"))))
