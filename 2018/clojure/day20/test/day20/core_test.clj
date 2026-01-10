(ns day20.core-test
  (:require [clojure.test :refer :all]
            [day20.core :refer :all]))

(deftest test-both-parts
  (testing "Day 20 Tests"
    (let [ast1 (parse-regex "ENWWW(NEEE|SSE(EE|N))")
          ast2 (parse-regex "ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN")
          ast3 (parse-regex "ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))")
          ast4 (parse-regex "WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))")
          ast-full (parse-regex (apply str (drop 1 (drop-last (drop-last (slurp "resources/day20.txt"))))))]
      (is (= 10 (p1 ast1)))
      (is (= 18 (p1 ast2)))
      (is (= 23 (p1 ast3)))
      (is (= 31 (p1 ast4)))
      (is (= 3835 (p1 ast-full)))
      (is (= 8520 (p2 ast-full))))))
