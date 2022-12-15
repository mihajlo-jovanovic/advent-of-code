(ns day13.core-test
  (:require [clojure.test :refer :all]
            [day13.core :refer :all]))

(deftest compare-pairs
  (testing "Comparing of pairs"
    (is (= true (compare-lists [1,1,3,1,1] [1,1,5,1,1])))
    (is (= true (compare-lists [[1],[2,3,4]] [[1],4])))
    (is (= false (compare-lists [9] [[8,7,6]])))
    (is (= true (compare-lists [[4,4],4,4] [[4,4],4,4,4])))
    (is (= false (compare-lists [7,7,7,7] [7,7,7])))
    (is (= true (compare-lists [] [3])))
    (is (= false (compare-lists [[[]]] [[]])))
    (is (= false (compare-lists [1,[2,[3,[4,[5,6,7]]]],8,9] [1,[2,[3,[4,[5,6,0]]]],8,9])))
    (is (= false (compare-lists [[[[10 6] 3 [9 6 7 9 7]] [[2 4 10 7 1] [7 9] [8 2 9 9 2] 5 [1]] 5 []]
                                 [[[8 6 6 9 1] 1] [7 [8 3] 9 4 [0 3 10 9 7]]]
                                 [[[10 1]] 0 [] [[4 1] [3] [10 6 4] 10]]
                                 [8 [7] 2 9]
                                 [[] 2 [[3 6 3 6] 4 [8 7 4 7 2] 3]]] [[8 9 [[]]] [] []])))
    )
  )
