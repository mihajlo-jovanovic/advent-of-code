(ns day12.core-test
  (:require [clojure.test :refer :all]
            [day12.core :refer :all]))

;; (deftest testing-validity-checker
;;   (testing "Day 12 tests"
;;     (is (valid? "#.#.###" [1,1,3]))
;;     (is (not (valid? "##..###" [1,1,3])))
;;     (is (not (valid? "##..###" [1,1,3])))
;;     (is (not (valid? ".##.###" [1,1,3])))
;;     (is (valid? ".#...#....###." [1,1,3]))
;;     (is (valid? "..#..#....###." [1,1,3]))
;;     (is (valid? ".#...#....###." [1,1,3]))
;;     (is (valid? ".#....#...###." [1,1,3]))
;;     (is (not (valid? ".#...##....##." [1,1,3])))))

(deftest testing-count-arrangements
  (testing "Day 12 tests"
    (is (= 1 (count-arrangements ["" '()])))
    (is (= 0 (count-arrangements ["" '(3)])))
    (is (= 1 (count-arrangements ["." '()])))
    (is (= 1 (count-arrangements ["???.###" '(1 1 3)])))
    (is (= 4 (count-arrangements [".??..??...?##." '(1 1 3)])))
    (is (= 1 (count-arrangements ["?#?#?#?#?#?#?#?" '(1 3 1 6)])))
    (is (= 1 (count-arrangements ["????.#...#..." '(4 1 1)])))
    (is (= 4 (count-arrangements ["????.######..#####." '(1 6 5)])))
    (is (= 10 (count-arrangements ["?###????????" '(3 2 1)])))))
