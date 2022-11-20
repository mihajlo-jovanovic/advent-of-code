(ns day3-clj.core-test
  (:require [clojure.test :refer :all]
            [day3-clj.core :refer :all]))

(deftest test-move
  (let [center {:R 0 :L 0 :U 0 :D 0}
        random {:R 3 :L 0 :U 7 :D 0}]
    (is (= {:R 0 :L 1 :U 0 :D 0} (move :L center)))
    (is (= {:R 1 :L 0 :U 0 :D 0} (move :R center)))
    (is (= {:R 0 :L 0 :U 0 :D 1} (move :D center)))
    (is (= {:R 0 :L 0 :U 1 :D 0} (move :U center)))
    (is (= {:R 4 :L 0 :U 7 :D 0} (move :R random)))
    (is (= {:R 2 :L 0 :U 7 :D 0} (move :L random)))))

(deftest test-coords
  (is (= [{:R 1, :L 0, :U 0, :D 0}
          {:R 2, :L 0, :U 0, :D 0}
          {:R 3, :L 0, :U 0, :D 0}]
         (coords :R 3 {:R 0 :L 0 :U 0 :D 0}))))

(deftest test-build-coords
  (testing "building coordswith sample input provided"
    (is (= '({:R 1, :L 0, :U 0, :D 0}
             {:R 2, :L 0, :U 0, :D 0}
             {:R 3, :L 0, :U 0, :D 0}
             {:R 4, :L 0, :U 0, :D 0}
             {:R 5, :L 0, :U 0, :D 0}
             {:R 6, :L 0, :U 0, :D 0}
             {:R 7, :L 0, :U 0, :D 0}
             {:R 8, :L 0, :U 0, :D 0}
             {:R 8, :L 0, :U 1, :D 0}
             {:R 8, :L 0, :U 2, :D 0}
             {:R 8, :L 0, :U 3, :D 0}
             {:R 8, :L 0, :U 4, :D 0}
             {:R 8, :L 0, :U 5, :D 0}
             {:R 7, :L 0, :U 5, :D 0}
             {:R 6, :L 0, :U 5, :D 0}
             {:R 5, :L 0, :U 5, :D 0}
             {:R 4, :L 0, :U 5, :D 0}
             {:R 3, :L 0, :U 5, :D 0}
             {:R 3, :L 0, :U 4, :D 0}
             {:R 3, :L 0, :U 3, :D 0}
             {:R 3, :L 0, :U 2, :D 0})
           (build-coords "R8,U5,L5,D3")))))
