(ns day4-clj.core-test
  (:require [clojure.test :refer :all]
            [day4-clj.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (are [x y] (= x y)
       true (day4 112233)
       false (day4 112232)
       true (day4 111122)
       false (day4 123444))

