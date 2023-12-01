(ns day2.core-test
  (:require [clojure.test :refer :all]
            [day2.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (let [input (parse-input "resources/input.txt")
          input-test (parse-input "resources/input_test.txt")]
      (is (= 8 (p1 input-test)))
      (is (= 2286 (p2 input-test)))
      (is (= 2149 (p1 input)))
      (is (= 71274 (p2 input))))))