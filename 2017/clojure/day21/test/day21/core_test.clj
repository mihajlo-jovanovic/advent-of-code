(ns day21.core-test
  (:require [clojure.test :refer :all]
            [day21.core :refer :all]))

(deftest rotate-and-flip
  (testing "Rotate and flip the image"
    (let [pattern (parse-image (slurp "resources/pattern.txt") "\n")]
      (is (= (:grid pattern) coords))
      (is (= #{[0 0] [0 1] [0 2] [1 2] [2 1]} (set (map #(rotate-90-cw % (dec (:size pattern))) (:grid pattern)))))
      (is (= (:grid (parse-image "###\n..#\n.#.\n" "\n")) (set (map #(flip-x % (dec (:size pattern))) (:grid pattern)))))
      (is (= (:grid pattern) (set (map (fn [x] (flip-x x 2)) (set (map #(flip-x % (dec (:size pattern))) (:grid pattern)))))))
      (is (= (:grid (parse-image ".#./#../###" "/")) (set (map #(flip-y % (dec (:size pattern))) (:grid pattern))))))))