(ns day5.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-rules [filename]
  (let [lines (s/split-lines (first (s/split (slurp filename) #"\n\n")))
        parse-line (fn [line] (mapv #(Integer/parseInt %) line))]
    (mapv parse-line (map (fn [line] (s/split line #"\|")) lines))))

(defn to-map [input]
  (reduce (fn [acc [k v]] (update acc v conj k)) {} input))

(defn parse-updates [filename]
  (let [lines (s/split-lines (second (s/split (slurp filename) #"\n\n")))
        parse-line (fn [line] (map #(Integer/parseInt %) line))]
    (map parse-line (map (fn [line] (s/split line #",")) lines))))

(defn valid-order? [order rules]
  (let [n (first order)
        remaining (rest order)]
    (if (empty? remaining)
      true
      (if (some #(contains? (rules n) %) remaining)
        false
        (valid-order? remaining rules)))))

(defn bring-to-front [seq el]
  (cons el (remove #{el} seq)))

(defn find-middle-element-p2 [rules nums]
  (loop [nums nums
         idx (quot (count nums) 2)]
    (let [n (first nums)
          remaining (rest nums)]
      (if (and (zero? idx) (not (some #(contains? (rules n) %) remaining)))
        n
        (if (some #(contains? (rules n) %) remaining)
          (let [to-bring-to-front (filter #(contains? (rules n) %) remaining)
                new-nums (reduce (fn [coll el] (bring-to-front coll el)) nums to-bring-to-front)]
            (recur new-nums idx))
          (recur remaining (dec idx)))))))

(defn part1 [rules updates]
  (let [middle-element (fn [coll] (nth coll (quot (count coll) 2)))]
    (reduce + (map middle-element (filter #(valid-order? % rules) updates)))))

(defn part2 [rules updates]
  (reduce + (map (partial find-middle-element-p2 rules) (filter #(not (valid-order? % rules)) updates))))

(defn -main
  []
  (let [rules (apply merge (map (fn [[k v]] {k (set v)}) (to-map (parse-rules "resources/input.txt"))))
        updates (parse-updates "resources/input.txt")]
    (println "Part 1: " (part1 rules updates))
    (time (println "Part 2: " (part2 rules updates)))))