(ns day6.core-test
  (:require [clojure.test :refer :all]
            [day6.core :refer :all]))

(deftest day6-test
  (testing "day 6 test."
    (is (= 4 (solve 7 9)))
    (is (= 8 (solve 15 40)))
    (is (= 9 (solve 30 200)))
    (is (= 32 (solve 47 282)))
    (is (= 25 (solve 70 1079)))
    (is (= 32 (solve 75 1147)))
    (is (= 11 (solve 66 1062)))
    (is (= 33875953 (solve 47707566 282107911471062)))))
