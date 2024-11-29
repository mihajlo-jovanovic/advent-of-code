(ns day9.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day9.core :refer [part1]]))

(deftest elf-game
  (testing "Elf game"
    (is (= 32 (part1 9 25)))
    (is (= 146373 (part1 13 7999)))
    (is (= 2764 (part1 17 1104)))
    (is (= 54718 (part1 21 6111)))
    (is (= 37305 (part1 30 5807)))))