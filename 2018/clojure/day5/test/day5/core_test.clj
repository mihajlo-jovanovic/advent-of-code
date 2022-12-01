(ns day5.core-test
  (:require [clojure.test :refer :all]
            [day5.core :refer :all]))

(deftest test-polar-opposite
  (testing "whether two units (chars) are of the same type but opposite polarity"
    (are [x y] (= x y)
               true (polar-opposite \a \A)
               false (polar-opposite \a \B)
               false (polar-opposite \a \a)
               false (polar-opposite \A \A))))

(deftest test-react
  (testing "whether two units (chars) are of the same type but opposite polarity"
    (are [x y] (= x y)
               "" (reduce react "aA")
               "" (reduce react "abBA")
               "abAB" (reduce react "abAB")
               "aabAAB" (reduce react "aabAAB")
               "dabCBAcaDA" (reduce react "dabAcCaCBAcCcaDA")
               10 (p1-alternate "dabAcCaCBAcCcaDA"))))