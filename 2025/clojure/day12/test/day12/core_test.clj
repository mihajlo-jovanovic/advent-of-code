(ns day12.core-test
  (:require [clojure.test :refer :all]
            [day12.core :refer :all]))

(deftest test-day12
  (testing "Day 12 test"
    (let [input-small (parse-input "resources/sample.txt")
          input-large (parse-input "resources/day12.txt")]
      (is (= 2 (solve-take2 input-small)))
      (is (= 526 (solve-take2 input-large))))))
