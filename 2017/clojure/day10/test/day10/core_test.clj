(ns day10.core-test
  (:require [clojure.test :refer :all]
            [day10.core :refer :all]))

(deftest knot-hash-test
  (testing "knot-hash"
    (let [input (clojure.string/trim-newline (slurp "resources/input.txt"))]
      (is (= (solve-p2 "") "a2582a3a0e66e6e86e3812dcb672a272"))
      (is (= (solve-p2 "AoC 2017") "33efeb34ea91902bb2f59c9920caa6cd"))
      (is (= (solve-p2 "1,2,3") "3efbe78a8d82f29979031a4aa0b16a9d"))
      (is (= (solve-p2 "1,2,4") "63960835bcdc130f0b66d7ff4f6a5a8e"))
      (is (= (solve-p2 input) "44f4befb0f303c0bafd085f97741d51d")))))