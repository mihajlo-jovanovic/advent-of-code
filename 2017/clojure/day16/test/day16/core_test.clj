(ns day16.core-test
  (:require [clojure.test :refer :all]
            [day16.core :refer :all]))

(deftest test-do-spin
  (testing "Spin dance move should rotate the programs"
    (is (= "" (do-spin "" 0)))
    (is (= "abcde" (do-spin "abcde" 0)))
    (is (= "eabcd" (do-spin "abcde" 1)))
    (is (= "cdeab" (do-spin "abcde" 3)))))

(deftest test-do-exchange
  (testing "Exchange dance move should swap the programs"
    (is (= "ba" (do-exchange "ab" 0 1)))
    (is (= "ab" (do-exchange "ba" 0 1)))
    (is (= "abcde" (do-exchange "abcde" 0 0)))
    (is (= "dabce" (do-exchange "eabcd" 0 4)))
    (is (= "eabdc" (do-exchange "eabcd" 3 4)))
    (is (= "eabdc" (do-exchange "eabcd" 4 3)))))