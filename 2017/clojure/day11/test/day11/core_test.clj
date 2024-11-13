(ns day11.core-test
  (:require [clojure.test :refer :all]
            [day11.core :refer :all]))

(deftest hex-grid
  (testing "hex grid"
    (let [input (clojure.string/trim-newline (slurp "resources/input.txt"))]
      (is (= (p1 "ne,ne,ne") 3))
      (is (= (p1 "ne,ne,sw,sw") 0))
      (is (= (p1 "ne,ne,s,s") 2))
      (is (= (p1 "se,sw,se,sw,sw") 3))
      (is (= (p1 input) 685))
      (is (= (p2 input) 1457)))))
