(ns day23.core-test
  (:require [clojure.test :refer :all]
            [day23.core :refer :all]))

(deftest tests-for-part-1-and-2
  (testing "Day 23 tests"
    (let [paths (parse-input (slurp "resources/input.txt"))
          slopes (parse-slopes (slurp "resources/input.txt"))
          ;; paths-full (parse-input (slurp "resources/day23.txt"))
          ;; slopes-full (parse-slopes (slurp "resources/day23.txt"))
          paths-p2 (parse-input-p2 (slurp "resources/input.txt"))
          g (build-reduced-graph paths-p2)]
      (is (= 94 (p1 paths slopes [1 0] [21 22])))
      ;; (is (= 2074 (p1 paths-full slopes-full [1 0] [139 140])))
      (is (= 154 (dfs-2 g successors [1 0] [21 22]))))))
