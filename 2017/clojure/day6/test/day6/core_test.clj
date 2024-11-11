(ns day6.core-test
  (:require [clojure.test :refer :all]
            [day6.core :refer :all]))

(deftest redistribute-blocks-test
  (testing "redistribute-blocks"
    (let [input [0 2 7 0]
          expected [2 4 1 2]
          already-seen [2 4 1 2]
          input-full [10	3	15	10	5	15	5	15	9	2	5	8	5	2	3	6]
          already-seen-full [1 1 0 15 14 13 12 10 10 9 8 7 6 4 3 5]]
      (is (= expected (redistribute-blocks input)))
      (is (= 5 (index-of-first-repeated (iterate redistribute-blocks input))))
      (is (= 4 (index-of-first-repeated (iterate redistribute-blocks already-seen))))
      (is (= 14029 (index-of-first-repeated (iterate redistribute-blocks input-full))))
      (is (= 2765 (index-of-first-repeated (iterate redistribute-blocks already-seen-full)))))))

(deftest index-of-first-repeated-test
  (testing "index-of-first-repeated"
    (is (= 4 (index-of-first-repeated [1 2 3 4 2 5])))
    (is (= 3 (index-of-first-repeated [3 5 6 3 7])))
    (is (= nil (index-of-first-repeated [1 2 3 4 5])))))
    
