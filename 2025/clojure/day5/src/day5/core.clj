(ns day5.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-input [filepath]
  (let [[ranges nums] (str/split (slurp filepath) #"\n\n")
        parse-range (fn [s] (let [[beg end] (str/split s #"-")] [(Long/parseLong beg) (Long/parseLong end)]))]
    {:ranges (->> ranges
                  s/split-lines
                  (map parse-range)) :ingredient-ids (->> nums
                                                          (str/split-lines)
                                                          (map Long/parseLong))}))
(defn p1 [{:keys [ranges ingredient-ids]}]
  (let [fresh? (fn [[beg end] ingredient] (<= beg ingredient end))]
    (count (filter (fn [ingredientID] (some #(fresh? % ingredientID) ranges)) ingredient-ids))))

(defn combine-ranges [ranges]
  (let
   [combine (fn [[beg1 end1 :as r1] [beg2 end2 :as r2]] (if (>= end1 beg2) [beg1 (max end1 end2)] [r1 r2]))]
    (loop [remaining (rest ranges)
           acc (conj [] (first ranges))]
      (if
       (empty? remaining)
        acc
        (let [a (last acc)
              b (first remaining)
              result (combine a b)]
          (if (sequential? (first result))  ;; could not combine
            (recur (rest remaining) (conj acc b))
            (recur (rest remaining) (conj (vec (drop-last acc)) result))))))))

(defn p2 [ranges]
  (let [combined (combine-ranges (sort ranges))
        count-in-range (fn [[beg end]] (- (inc end) beg))]
    (->> combined
         (map count-in-range)
         (reduce +))))

(defn -main []
  (let [input (parse-input "resources/day5.txt")]
    (time (println "Part 1: " (p1 input)))
    (time (println "Part 2: " (p2 (:ranges input))))))
