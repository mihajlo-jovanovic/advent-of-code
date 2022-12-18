(ns day11.core-test
  (:require [clojure.test :refer :all]
            [day11.core :refer :all]))

(deftest monkey-throwing
  (testing "Monkey throwing items to other monkeys"
    (is (= [3 500] (monkey-throw 0 79)))
    (is (= [3 620] (monkey-throw 0 98)))
    (is (= [0 20] (monkey-throw 1 54)))
    (is (= [0 23] (monkey-throw 1 65)))
    (is (= [0 27] (monkey-throw 1 75)))
    (is (= [0 26] (monkey-throw 1 74)))
    (is (= [1 2080] (monkey-throw 2 79)))
    (is (= [3 1200] (monkey-throw 2 60)))
    (is (= [3 3136] (monkey-throw 2 97)))
    (is (= [1 25] (monkey-throw 3 74)))
    (is (= [1 167] (monkey-throw 3 500)))
    (is (= [1 207] (monkey-throw 3 620)))))
