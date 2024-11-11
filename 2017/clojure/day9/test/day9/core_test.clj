(ns day9.core-test
  (:require [clojure.test :refer :all]
            [day9.core :refer :all]))

(deftest process-stream-test
  (testing "process-stream"
    (let [initial-state {:score 0 :depth 0 :garbage false :exclamation false :count 0}
          char-stream (slurp "resources/input.txt")]
      ;; (is (= (process-stream initial-state "{}") 1))
      ;; (is (= (process-stream initial-state "{{{}}}") 6))
      ;; (is (= (process-stream initial-state "{{},{}}") 5))
      ;; (is (= (process-stream initial-state "{{{},{},{{}}}}") 16))
      ;; ;; {<a>,<a>,<a>,<a>}, score of 1.
      ;; ;; {{<ab>},{<ab>},{<ab>},{<ab>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
      ;; ;; {{<!!>},{<!!>},{<!!>},{<!!>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
      ;; ;; {{<a!>},{<a!>},{<a!>},{<ab>}}, score of 1 + 2 = 3
      ;; (is (= (process-stream initial-state "{<a>,<a>,<a>,<a>}") 1))
      ;; (is (= (process-stream initial-state "{{<ab>},{<ab>},{<ab>},{<ab>}}") 9))
      ;; (is (= (process-stream initial-state "{{<!!>},{<!!>},{<!!>},{<!!>}}") 9))
      ;; (is (= (process-stream initial-state "{{<a!>},{<a!>},{<a!>},{<ab>}}") 3))
      ;; (is (= (process-stream initial-state char-stream) 9662))

      (is (= (process-stream initial-state "<>") 0))
      (is (= (process-stream initial-state "<random characters>") 17))
      (is (= (process-stream initial-state "<<<<>") 3))
      (is (= (process-stream initial-state "<{!>}>") 2))
      (is (= (process-stream initial-state "<!!>") 0))
      (is (= (process-stream initial-state "<!!!>>") 0))
      (is (= (process-stream initial-state "<{o\"i!a,<{i<a>") 10))
      (is (= (process-stream initial-state char-stream) 4903)))))
