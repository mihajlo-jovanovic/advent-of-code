(ns day20.core-test
  (:require [clojure.test :refer :all]
            [day20.core :refer :all]))

(deftest test-new-idx
  (testing "Calculating new index position"
    (is (= 1 (new-idx 0 1 7)))
    (is (= 2 (new-idx 0 2 7)))
    (is (= 4 (new-idx 1 -3 7)))
    (is (= 4 (new-idx 1 -15 7)))
    (is (= 5 (new-idx 2 3 7)))
    (is (= 6 (new-idx 2 -2 7)))
    (is (= 5 (new-idx 2 -3 7)))
    (is (= 3 (new-idx 3 0 7)))
    (is (= 3 (new-idx 5 4 7)))
    (is (= 2 (new-idx 1 7 7)))
    ))

(deftest test-part1
  (testing "part 1 & 2"
    (is (= 3 (solve1 start-state)))
    (is (= 1623178306 (solve2 start-state)))
    ;(is (= 4224 (solve1 start-state-lg)))
    ;(is (= 861907680486 (solve2 start-state-lg)))
    ))