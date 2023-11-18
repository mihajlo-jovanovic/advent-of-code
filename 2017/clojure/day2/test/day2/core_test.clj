(ns day2.core-test
  (:require [clojure.test :refer :all]
            [day2.core :refer :all]))

(deftest evenly-div-test
  (testing "evenly-div"
    (is (= 4 (evenly-div [5 9 2 8])))
    (is (= 3 (evenly-div [9 4 7 3])))))