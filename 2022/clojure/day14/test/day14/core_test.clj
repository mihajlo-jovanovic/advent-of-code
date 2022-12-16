(ns day14.core-test
  (:require [clojure.test :refer :all]
            [day14.core :refer :all]))

(deftest movement
  (testing "Sand falling down."
    (let [sand [500 0]
          cave (into (hash-set) [[497, 6], [498, 6], [502, 7], [494, 9], [499, 9], [503, 4], [498, 9], [502, 5], [497, 9], [498, 4], [500, 9], [502, 4], [501, 9], [502, 6], [498, 5], [502, 8], [496, 6], [495, 9], [496, 9], [502, 9]])]
      (is (= [500 1] (down sand cave 11)))
      (is (= [500 8] (down [500 8] cave 11)))
      (is (= [100 1] (down [100 0] cave 11)))
      (is (= [499 1] (left sand cave 11)))
      (is (= [500 8] (left [500 8] cave 11)))
      (is (= [100 10] (left [100 10] cave 11))))))
