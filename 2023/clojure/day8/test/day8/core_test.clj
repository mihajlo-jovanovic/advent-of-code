(ns day8.core-test
  (:require [clojure.test :refer :all]
            [day8.core :refer :all]))

(deftest day8-test
  (testing "Day 8 tests"
    (let [input-test1 (parse-input (slurp "resources/input_test1.txt"))
          instructions-test1 (parse-instructions (slurp "resources/input_test1.txt"))
          input-test2 (parse-input (slurp "resources/input_test2.txt"))
          instructions-test2 (parse-instructions (slurp "resources/input_test2.txt"))
          input-test3 (parse-input (slurp "resources/input_test3.txt"))
          instructions-test3 (parse-instructions (slurp "resources/input_test3.txt"))
          input (parse-input (slurp "resources/day8.txt"))
          instructions (parse-instructions (slurp "resources/day8.txt"))]
      (is (= 2 (p1 (partial lookup input-test1) instructions-test1 "AAA")))
      (is (= 6 (p1 (partial lookup input-test2) instructions-test2 "AAA")))
      (is (= 15989 (p1 (partial lookup input) instructions "AAA")))
      (is (= 6 (reduce lcm (map (partial p2 (partial lookup input-test3) instructions-test3) ["11A", "22A"]))))
      (is (= 13830919117339 (reduce lcm (map (partial p2 (partial lookup input) instructions) ["CQA" "BLA" "DFA" "PQA" "TGA" "AAA"])))))))