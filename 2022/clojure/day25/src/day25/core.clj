(ns day25.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(defn digit [ch]
  "Converts SNAFU digit (Character) to a decimal digit: -2, -1, 0, 1, 2"
  (if (= \= ch)
    -2
    (Character/digit ch 10)))

(defn digit-snafu [n]
  "the other way - decimal to SNAFU"
  (case n
    -2 \=
    -1 \-
    n))

(defn snafu-to-decimal [n]
  (long (reduce +
                (map
                  #(* (Math/pow 5 (first %)) (digit (second %)))
                  (map-indexed vector (reverse n))))))

(defn decimal-to-snafu-rec [n acc]
  "A bit ugly, but hey...been a looong month..."
  (if (<= -2 n 2)                                           ;; n in range - single digit
    (str acc (digit-snafu n))
    (let [coll (reductions + (map #(* 2 (long (Math/pow 5 %))) (range 0 20)))
          p (first (first (filter #(< (abs n) (second %)) (map-indexed vector coll))))
          m (nth coll (dec p))
          tmp (long (Math/pow 5 p))
          c (first (filter #(<= (abs (- n (* tmp %))) m) '(-2 -1 0 1 2)))
          new-n (- n (* c tmp))
          new-p (first (first (filter #(< (abs new-n) (second %)) (map-indexed vector coll))))]
      (recur new-n (if (= new-p (- p 2)) (str acc (digit-snafu c) 0)
                                         (str acc (digit-snafu c)))))))

(defn -main
  [& _]
  (let [snafu-nums (map #(into (vector) (char-array %)) (-> "day25.txt"
                                                            io/resource
                                                            slurp
                                                            str/split-lines))]
    (println (decimal-to-snafu-rec (reduce + (map snafu-to-decimal snafu-nums)) ""))))