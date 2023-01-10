(ns day24.core-test
  (:require [clojure.test :refer :all]
            [day24.core :refer :all]))

(deftest parsing-a-single-line
  (testing "Parsing a single line of the map."
    (is (= '({:d \>
              :x 0
              :y 0}
             {:d \>
              :x 0
              :y 1}
             {:d \<
              :x 0
              :y 3}
             {:d \^
              :x 0
              :y 4}
             {:d \<
              :x 0
              :y 5}) (parse-line [0 "#>>.<^<#"])))))

(deftest parts-1-and-2-solutions
  (testing "Both part solutions."
    (let [input "#.######\n#>>.<^<#\n#.<..<<#\n#>v.><>#\n#<^v^^>#\n######.#"
          init-state (merge {:e start-pos} (parse-blizzards input))]
      (is (= 18 (solve1 init-state)))
      (is (= 54 (solve2 init-state))))))
