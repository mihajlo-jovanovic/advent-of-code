(ns day17.core-test
  (:require [clojure.test :refer :all]
            [day17.core :refer :all]))

(deftest spinlock-test
  (testing "spinlock buffer insertion"
    (let [spinlock-sample (partial spinlock 3)
          spinlock (partial spinlock 337)
          initial-buffer-state {:buffer '(0) :index 0}]
      (is (= 638 (->> (iterate spinlock-sample initial-buffer-state)
                      (take 2018)
                      (last)
                      :buffer
                      (drop-while #(not= 2017 %))
                      (second))))
      (is (= 600 (->> (iterate spinlock initial-buffer-state)
                      (take 2018)
                      (last)
                      :buffer
                      (drop-while #(not= 2017 %))
                      (second)))))))