(ns day24.core-test
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [day24.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (let [input-sample (parse-input (slurp "resources/input-test.txt"))
          input-full (parse-input (slurp "resources/day24.txt"))]
      (is (= 2 (p1 input-sample [7 27])))
      (is (= 17906 (p1 input-full [200000000000000 400000000000000])))
            (is (= 47 (p2 input-sample)))
      (is (= 571093786416929 (p2 input-full)))
      )))
