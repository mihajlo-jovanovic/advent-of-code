(ns day24.core-test
  (:require [clojure.test :refer :all]
            [day24.core :refer :all]))

(deftest test-part1
  (testing "part 2"
    (is (= 5216 (p1 (parse-input "resources/sample.txt"))))))
