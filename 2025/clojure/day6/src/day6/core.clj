(ns day6.core
  (:require [clojure.string :as str]))

(defn parse-input [filepath]
  (str/split-lines (slurp filepath)))

(defn p1 [input]
  (let [numbers (into [] (drop-last input))
        operations (re-seq #"[*+]" (last input))
        parse-numbers (fn [s] (map Integer/parseInt (re-seq #"-?\d+" s)))
        numbers-parsed (map parse-numbers numbers)
        zipped (map vector (apply map vector numbers-parsed) operations)
        solve-group (fn [[nums op]] (if (= "+" op) (reduce + nums) (reduce * nums)))]
    (reduce + (map solve-group zipped))))

(defn transpose [lines]
  (if (empty? lines)
    []
    (let [max-width (apply max (map count lines))
          padded-lines (map #(format (str "%-" max-width "s") %) lines)]
      (apply map str padded-lines))))

(defn solve [problem]
  (let [op (last (first problem))
        form (->> problem
                  (map #(if (re-find #"[*+]" %) (apply str (drop-last %)) %))
                  (str/join " "))]
    (eval (read-string (str "(" op " " form ")")))))

(defn p2 [input]
  (reduce + (map solve (remove #(empty? (str/trim (first %))) (partition-by #(empty? (str/trim %)) (transpose input))))))
