(ns day7.core-test
  (:require [clojure.test :refer :all]
            [day7.core :refer :all]))

(deftest day7-tests
  (testing "find root"
    (let [sample-input (slurp "resources/sample.txt")
          input (slurp "resources/day7.txt")
          sample-tree (parse-inputs sample-input)
          tree (parse-inputs input)]
      (is (= (find-root sample-tree) "tknk"))
      (is (= (find-root tree) "hlhomy"))
      (is (= (solve-p2 sample-tree "tknk" 0) 60))
      (is (= (solve-p2 tree "hlhomy" 0) 1505)))))
