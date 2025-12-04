(ns day2.core
  (:require [clojure.string :as s])
  (:gen-class))

(defn parse-input [filepath]
  (let [parse-interval (fn [s] (mapv Long/parseLong (s/split s #"-")))]
    (->> (s/split (s/trim (slurp filepath)) #",")
         (mapv parse-interval))))

(defn count-invalid-helper [firstID lastID cnt-digits]
  (let [exp (fn [x n]
              (loop [acc 1 n n]
                (if (zero? n) acc
                    (recur (* x acc) (dec n)))))
        pow-of-10 (exp 10 (/ cnt-digits 2))
        beg (if (= cnt-digits (count (str firstID))) (quot firstID pow-of-10) (exp 10 (dec (/ cnt-digits 2))))
        end (if (= cnt-digits (count (str lastID)))  (quot lastID pow-of-10) (dec pow-of-10))
        beg2 (+ (* beg pow-of-10) beg)
        end2 (+ (* end pow-of-10) end)
        beg3 (if (>= beg2 firstID) beg (inc beg))
        end3 (if (<= end2 lastID) end (dec end))]
    (reduce + (map #(+ (* % pow-of-10) %) (range beg3 (inc end3))))))

(defn count-invalid [[firstID lastID]]
  (let [cnt-digits-firstID (count (str firstID))
        cnt-digits-lastID (count (str lastID))
        cnt-digits (filter even? (range cnt-digits-firstID (inc cnt-digits-lastID)))]
    (if (not (seq cnt-digits))
      0
      (count-invalid-helper firstID lastID (first cnt-digits)))))

(defn p1 [id-ranges]
  (reduce + (map count-invalid id-ranges)))

;; Part 2
(defn divisible-by? [number divisor]
  (zero? (rem number divisor)))

(defn is-invalid? [s]
  (let [repeated-digits-length-ranges (fn [s] (filter #(divisible-by? (count s) %) (range 1 (inc (quot (count s) 2)))))]
    (->> (repeated-digits-length-ranges s)
         (map #(apply = (partition % s)))
         (some true?))))

(defn p2 [id-ranges]
  (let [filter-invalid (fn [[firstID lastID]] (filter #(is-invalid? (str %)) (range firstID (inc lastID))))]
    (->> id-ranges
         (map filter-invalid)
         flatten
         (reduce +))))

(defn -main []
  (let [input (parse-input "resources/day2.txt")]
    (time (println "Part 1: " (p1 input)))
    (time (println "Part 1: " (p2 input)))))
