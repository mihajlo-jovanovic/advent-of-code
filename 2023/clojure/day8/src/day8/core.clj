(ns day8.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-input [input]
  (let [lines (drop 2 (str/split-lines input))
        input-map (zipmap (map #(first (str/split % #" = ")) lines) (map #(second (str/split % #" = ")) lines))]
    (update-vals input-map (fn [val] (second (map #(str/split % #", ") (str/split val #"[\(\)]")))))))

(defn parse-instructions [input] (cycle (first (clojure.string/split-lines input))))

(defn lookup [m key instruction]
  (let [val (get m key)]
    (if (= \L instruction) (first val) (second val))))

(defn p1 [f seq init-val]
  (loop [init-val init-val
         seq seq
         count 1]
    (let [instruction (first seq)
          result (f init-val instruction)]
      (if (= "ZZZ" result)
        count
        (recur result (rest seq) (inc count))))))

(defn p2 [f seq init-val]
  (loop [init-val init-val
         seq seq
         count 1]
    (let [instruction (first seq)
          result (f init-val instruction)]
      (if (clojure.string/ends-with? result "Z")
        count
        (recur result (rest seq) (inc count))))))

(defn gcd
  "Greatest common divisor of two integers"
  [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

(defn lcm
  "Least common multiple of two integers"
  [a b]
  (/ (Math/abs (* a b)) (gcd a b)))

(defn -main []
  (let [input (slurp "resources/day8.txt")
        input-map (parse-input input)
        instructions (parse-instructions input)]
    (println (p1 (partial lookup input-map) instructions "AAA"))
    (println (reduce lcm (map (partial p2 (partial lookup input-map) instructions) ["CQA" "BLA" "DFA" "PQA" "TGA" "AAA"])))))