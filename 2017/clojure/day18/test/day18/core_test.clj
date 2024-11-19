(ns day18.core-test
  (:require [clojure.test :refer :all]
            [day18.core :refer :all]))

(deftest proccess-instructions-test
  (testing "processing instructions"
    (let [state {:registers (zipmap (map #(str (char %)) (range 97 123)) (repeat 0))
                 :send-queue clojure.lang.PersistentQueue/EMPTY
                 :recv-queue clojure.lang.PersistentQueue/EMPTY
                 :pc 0
                 :sent-count 0
                 :suspended false}
          instruction-set {:Type :set :Op1 "a" :Op2 "1"}
          instruction-add {:Type :add :Op1 "a" :Op2 "2"}
          instruction-mul {:Type :mul :Op1 "a" :Op2 "a"}
          instruction-mod {:Type :mod :Op1 "a" :Op2 "5"}
          instruction-jmp {:Type :jgz :Op1 "a" :Op2 "-1"}
          state-after-set (process-instruction state instruction-set)
          state-after-add (process-instruction state-after-set instruction-add)
          state-after-mul (process-instruction state-after-add instruction-mul)
          state-after-mod (process-instruction state-after-mul instruction-mod)
          state-after-jmp (process-instruction state-after-mod instruction-jmp)]
      (is (= 1 (get (:registers state-after-set) "a")))
      (is (= 3 (get (:registers state-after-add) "a")))
      (is (= 2 (:pc state-after-add)))
      (is (= 1 (:pc state-after-set)))
      (is (= 3 (:pc state-after-mul)))
      (is (= 4 (:pc state-after-mod)))
      (is (= 3 (:pc state-after-jmp)))
      (is (= 4 (get (:registers state-after-mod) "a"))))))

(deftest run-program-test
  (testing "Part 2 - two programs"
    (let [instructions (vec (map parse-instruction (clojure.string/split-lines (slurp "resources/input-sample.txt"))))
          state {:registers (initalize-registers)
                 :send-queue clojure.lang.PersistentQueue/EMPTY
                 :recv-queue clojure.lang.PersistentQueue/EMPTY
                 :pc 0
                 :sent-count 0
                 :suspended false}
          state-after-run (run-program state instructions)]
      (is (= 3 (:sent-count state-after-run)))
      (is (= 3 (:pc state-after-run)))
      (is (= true (:suspended state-after-run))))))

(deftest part-2-test
  (testing "Part 2 - two programs"
    (is (= 7620 (solve-p2 "resources/input.txt")))))