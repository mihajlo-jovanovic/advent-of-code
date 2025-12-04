(ns day2.core
  (:require [clojure.string :as s])
  (:gen-class))

(defn parse-input [filepath]
  (let [parse-interval (fn [s] (mapv Long/parseLong (s/split s #"-")))]
    (->> (s/split (s/trim (slurp filepath)) #",")
         (mapv parse-interval))))

;; Part 1

(defn- is-invalid-p1? [n]
  (let [s (str n)
        cnt (count s)]
    (and (even? cnt)
         (let [half (/ cnt 2)
               [first-half second-half] (split-at half s)]
           (= first-half second-half)))))

(defn p1 [id-ranges]
  (let [filter-invalid (fn [[start end]]
                         (filter is-invalid-p1? (range start (inc end))))]
    (->> id-ranges
         (mapcat filter-invalid)
         (reduce +))))

;; Part 2

(defn- divisible-by? [number divisor]
  (zero? (rem number divisor)))

(defn- is-invalid-p2? [s]
  (let [len (count s)
        possible-chunk-sizes (filter #(divisible-by? len %) (range 1 (inc (quot len 2))))]
    (some (fn [chunk-size]
            (apply = (partition chunk-size s)))
          possible-chunk-sizes)))

(defn p2 [id-ranges]
  (let [filter-invalid (fn [[start end]]
                         (filter #(is-invalid-p2? (str %)) (range start (inc end))))]
    (->> id-ranges
         (mapcat filter-invalid)
         (reduce +))))

(defn -main []
  (let [input (parse-input "resources/day2.txt")]
    (time (println "Part 1: " (p1 input)))
    (time (println "Part 2: " (p2 input)))))
