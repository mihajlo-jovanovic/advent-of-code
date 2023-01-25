(ns day11.core-test
  (:require [clojure.test :refer :all]
            [day11.core :refer :all]))

; Uncomment sample input (monkeys 1-4) for tests to pass
;(deftest single-monkey-turn
;  (testing "Single monkey's turn of throwing all its items"
;    (is (= [74 500 620] (:items ((turn monkeys 0) 3))))
;    (is (= [20, 23, 27, 26] (:items ((turn (turn (turn (turn monkeys 0) 1) 2) 3) 0))))
;    (is (= [2080, 25, 167, 207, 401, 1046] (:items ((turn (turn (turn (turn monkeys 0) 1) 2) 3) 1))))))