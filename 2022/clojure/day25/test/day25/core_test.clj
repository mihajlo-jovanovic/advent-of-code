(ns day25.core-test
  (:require [clojure.test :refer :all]
            [day25.core :refer :all]))

(deftest converting-to-snafu
  (testing "Converting SNAFU numbers to decimal."
    (is (= 1747 (snafu-to-decimal (into (vector) (char-array "1=-0-2")))))
    (is (= 906 (snafu-to-decimal (into (vector) (char-array "12111")))))
    (is (= 198 (snafu-to-decimal (into (vector) (char-array "2=0=")))))
    (is (= 11 (snafu-to-decimal (into (vector) (char-array "21")))))
    (is (= 201 (snafu-to-decimal (into (vector) (char-array "2=01")))))
    (is (= 31 (snafu-to-decimal (into (vector) (char-array "111")))))
    (is (= 1257 (snafu-to-decimal (into (vector) (char-array "20012")))))
    (is (= 32 (snafu-to-decimal (into (vector) (char-array "112")))))
    (is (= 353 (snafu-to-decimal (into (vector) (char-array "1=-1=")))))
    (is (= 107 (snafu-to-decimal (into (vector) (char-array "1-12")))))
    (is (= 7 (snafu-to-decimal (into (vector) (char-array "12")))))
    (is (= 3 (snafu-to-decimal (into (vector) (char-array "1=")))))
    (is (= 37 (snafu-to-decimal (into (vector) (char-array "122")))))
    (is (= 314159265 (snafu-to-decimal (into (vector) (char-array "1121-1110-1=0")))))))

(deftest converting-decimal-to-snafu
  (testing "Converting decimal back to SNAFU."
    (is (= "1" (decimal-to-snafu-rec 1 "")))
    (is (= "2" (decimal-to-snafu-rec 2 "")))
    (is (= "1=" (decimal-to-snafu-rec 3 "")))
    (is (= "1-" (decimal-to-snafu-rec 4 "")))
    (is (= "10" (decimal-to-snafu-rec 5 "")))
    (is (= "11" (decimal-to-snafu-rec 6 "")))
    (is (= "12" (decimal-to-snafu-rec 7 "")))
    (is (= "2=" (decimal-to-snafu-rec 8 "")))
    (is (= "2-" (decimal-to-snafu-rec 9 "")))
    (is (= "20" (decimal-to-snafu-rec 10 "")))
    (is (= "1=0" (decimal-to-snafu-rec 15 "")))
    (is (= "1-0" (decimal-to-snafu-rec 20 "")))
    (is (= "1=11-2" (decimal-to-snafu-rec 2022 "")))
    (is (= "1-0---0" (decimal-to-snafu-rec 12345 "")))
    (is (= "1121-1110-1=0" (decimal-to-snafu-rec 314159265 "")))))
