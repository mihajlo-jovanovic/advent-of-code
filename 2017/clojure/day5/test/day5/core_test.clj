(ns day5.core-test
  (:require [clojure.test :refer :all]
            [day5.core :refer :all]))

(deftest day5-test
  (testing "jump function"
    (deftest test-jump-state)
    (testing "jump function with state map"
      (let [filename "resources/day5.txt"
            state (make-state (parse-input filename))]
        (is (= (jump-v1 {:list [0 3 0 1 -3] :index 0}) 5))
        (is (= (jump-v1 state) 339351))
        (is (= (jump-v2 {:list [0 3 0 1 -3] :index 0}) 5))
        (is (= (jump-v2 state) 339351))
        (is (= (jump {:list [0 3 0 1 -3] :index 0} update-state-p1) 5))
        (is (= (jump state update-state-p1) 339351))
        (is (= (jump state update-state-p2) 24315397))))))