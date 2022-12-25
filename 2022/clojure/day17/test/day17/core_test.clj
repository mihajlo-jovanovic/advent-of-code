(ns day17.core-test
  (:require [clojure.test :refer :all]
            [day17.core :refer :all]))

(deftest test-part1
  (testing "How tall is the Rock Tower?"
    (let [state {:step 0 :step-jet 0 :jet-pattern ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>" :chamber #{}}]
      (is (= 3175 (p1 2023 state))))))
