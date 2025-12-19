(ns day8.core-test
  (:require [clojure.test :refer :all]
            [day8.core :refer :all]))

(deftest test-day8
  (testing "Day 8"
    (let [nodes-sm (parse-input "resources/sample.txt")
          nodes-lg (parse-input "resources/day8.txt")
          edges-sm (edges-seq nodes-sm)
          edges-lg (edges-seq nodes-lg)]
      (is (= 40 (p1 nodes-sm edges-sm 10)))
     ;; (is (= 54180 (p1 nodes-lg edges-lg 1000)))
      (is (= 25272 (p2 edges-sm (count nodes-sm))))
      (is (= 25325968 (p2 edges-lg (count nodes-lg)))))))
