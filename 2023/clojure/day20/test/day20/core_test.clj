(ns day20.core-test
  (:require [clojure.test :refer :all]
            [day20.core :refer :all]))

(deftest test-receive
  (testing "Tests for receive multi-methods"
    (let [broadcaster {:t :broadcaster :d [:a :b :c]}
          a {:t :flip-flop :d [:b] :state false}
          a1 {:t :flip-flop :d [:b] :state true}
          b {:t :flip-flop :d [:c] :state true}
          inv {:t :conjunction :d [:a]}
          inv2 {:t :conjunction :d [:a] :state {:c false}}]
      (is (= {:state nil :out '([:a false] [:b false] [:c false])} (receive broadcaster [:broadcaster false])))
      (is (= {:state true :out '([:b true])} (receive a [:broadcaster false])))
      (is (= {:state false :out '([:b false])} (receive a1 [:broadcaster false])))
      (is (nil? (receive a [:broadcaster true])))
      (is (= {:state false :out '([:c false])} (receive b [:broadcaster false])))
      (is (= {:state {:c false} :out '([:a true])} (receive inv [:c false])))
      (is (= {:state {:c true} :out '([:a false])} (receive inv2 [:c true]))))))

(deftest test-part1
  (testing "Day 20 tests - part 1"
    (let [input-sample1 (slurp "resources/input-sample.txt")
          input-sample2 (slurp "resources/input-sample2.txt")
          input-full (slurp "resources/day20.txt")]
      (is (= 32000000 (part1 input-sample1)))
      (is (= 11687500 (part1 input-sample2)))
      (is (= 898731036 (part1 input-full)))
      (is (= 229414480926893 (part2 input-full))))))
