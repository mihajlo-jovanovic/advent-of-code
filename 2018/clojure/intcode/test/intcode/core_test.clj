(ns intcode.core-test
  (:require [clojure.test :refer :all]
            [intcode.core :refer :all]))

(deftest intcode-computer-tests 
  (testing "read stdin/write stdout"
    (is (= 1 (-> {:memory [3 0 99] :in [1]}
                 intcode-vm-new
                 :memory
                 (get 0))))
    (is (= 4 (-> {:memory [4 0 99]}
                 intcode-vm-new
                 :out
                 first)))
    (is (= :hello (-> {:memory [3 0 4 0 99] :in [:hello]}
                    intcode-vm-new
                    :out
                    first)))))
