(ns day16.core-test
  (:require [clojure.test :refer :all]
            [day16.core :refer :all]))

(deftest addr-test
  (is (= [3 2 1 5] (addr [0 0 1 3] [3 2 1 1]))))

(deftest addi-test
  (is (= [3 2 1 4] (addi [0 0 1 3] [3 2 1 1]))))

(deftest mulr-test
  (is (= [3 2 1 1] (mulr [0 2 3 3] [3 2 1 1]))))

(deftest muli-test
  (is (= [3 2 1 3] (muli [0 2 3 3] [3 2 1 1]))))

(deftest banr-test
  (is (= [3 2 1 1] (banr [0 2 3 3] [3 2 1 1]))))

(deftest bani-test
  (is (= [3 2 1 1] (bani [0 2 3 3] [3 2 1 1]))))

(deftest borr-test
  (is (= [3 2 1 1] (borr [0 2 3 3] [3 2 1 1]))))

(deftest bori-test
  (is (= [3 2 1 3] (bori [0 2 3 3] [3 2 1 1]))))

(deftest setr-test
  (is (= [3 2 1 3] (setr [0 0 0 3] [3 2 1 1]))))

(deftest seti-test
  (is (= [3 2 1 9] (seti [0 9 0 3] [3 2 1 1]))))

(deftest gtir-test
  (is (= [3 2 1 1] (gtir [0 5 1 3] [3 2 1 1])))
  (is (= [3 2 1 0] (gtir [0 1 1 3] [3 2 1 1]))))

(deftest gtri-test
  (is (= [3 2 1 1] (gtri [0 1 1 3] [3 2 1 1])))
  (is (= [3 2 1 0] (gtri [0 1 5 3] [3 2 1 1]))))

(deftest gtrr-test
  (is (= [3 2 1 1] (gtrr [0 0 1 3] [3 2 1 1])))
  (is (= [3 2 1 0] (gtrr [0 1 0 3] [3 2 1 1]))))

(deftest eqir-test
  (is (= [3 2 1 1] (eqir [0 2 1 3] [3 2 1 1])))
  (is (= [3 2 1 0] (eqir [0 5 1 3] [3 2 1 1]))))

(deftest eqri-test
  (is (= [3 2 1 1] (eqri [0 1 2 3] [3 2 1 1])))
  (is (= [3 2 1 0] (eqri [0 1 5 3] [3 2 1 1]))))

(deftest eqrr-test
  (is (= [3 2 1 1] (eqrr [0 2 2 3] [3 2 1 1])))
  (is (= [3 2 1 0] (eqrr [0 0 1 3] [3 2 1 1]))))

(deftest parse-sample-test
  (let [input "Before: [3, 2, 1, 1]\n9 2 1 2\nAfter:  [3, 2, 2, 1]"
        expected {:before [3 2 1 1]
                  :instruction [9 2 1 2]
                  :after [3 2 2 1]}]
    (is (= expected (parse-sample input)))))

(deftest p1-test
  (is (= 607 (p1))))

(deftest p2-test
  (is (= [577, 18 1 577] (p2))))
