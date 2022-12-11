(ns day10.core-test
  (:require [clojure.test :refer :all]
            [day10.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 4 (-> {:memory ["noop" "addx 3" "addx -5"], :x 1, :counter 1}
                 run-vm-cycle
                 run-vm-cycle
                 run-vm-cycle
                 :x)))))
