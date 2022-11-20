(ns day4-clj.core
  (:require clojure.string :as str))

(defn day4
  [n]
  (let [two-adj-same?
        (->> (str/split (str n) #"")
             (map #(Integer/parseInt %))
             (partition 2 1)
             (filter #(= (first %) (second %)))
             (empty?)
             (not))
        increasing?
        (->> (str/split (str n) #"")
             (map #(Integer/parseInt %))
             (partition 2 1)
             (filter #(> (first %) (second %)))
             (empty?))
        ]
    (and two-adj-same? increasing?)))

(defn day4-2
  [n]
  (let [[a b c d e f] n]
    (or (and (= a b) (not (= b c)))
        (and (= b c) (not (= a b)) (not (= c d)))
        (and (= c d) (not (= b c)) (not (= d e)))
        (and (= d e) (not (= c d)) (not (= e f)))
        (and (= e f) (not (= d e))))))

(defn helper
  [col]
  (map #(Integer/parseInt %) col))
