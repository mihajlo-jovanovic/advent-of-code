(ns day17.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [day17.core :refer [parse-input parse-instruction part2-recursive]]))

(deftest day17-part2
  (testing "Day 17: finding the value for A that results in the program outputting itself"
    (let [program-str1 "0,3,5,4,3,0"
          program1 (parse-input program-str1)
          instructions1 (into [] (map parse-instruction (partition 2 program1)))
          program-str2 "2,4,1,2,7,5,4,5,1,3,5,5,0,3,3,0"
          program2 (parse-input program-str2)
          instructions2 (into [] (map parse-instruction (partition 2 program2)))]
      (is (= 117440 (part2-recursive (reverse program1) 0 program1 instructions1)))
      (is (= 37221270076916 (part2-recursive (reverse program2) 0 program2 instructions2))))))
