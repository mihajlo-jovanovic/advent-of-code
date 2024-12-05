(ns day5.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-line [line]
  (map #(Integer/parseInt %) line))

(defn parse-rules [lines]
  (map parse-line (map #(s/split % #"\|") lines)))

(defn parse-updates [lines]
  (map parse-line (map #(s/split % #",") lines)))

(defn parse-input
  "Returns a map with the rules and the updates as keys. Rules is a map with the page numbers as keys 
   and sets of prerequisite page numbers as values. Updates is a list of lists of page numbers."
  [filename]
  (let [parts (s/split (slurp filename) #"\n\n")
        rules (parse-rules (s/split-lines (first parts)))
        rules-map  (reduce (fn [acc [k v]] (update acc v (fnil conj #{}) k)) {} rules)]
    {:rules rules-map
     :updates (parse-updates (s/split-lines (second parts)))}))

;; could prob simplify this by using every? and not-any?
(defn valid-order? [order rules]
  (let [n (first order)
        remaining (rest order)]
    (if (empty? remaining)
      true
      (if (some #(contains? (rules n) %) remaining)
        false
        (valid-order? remaining rules)))))

(defn find-middle-element-p2 [rules nums]
  (letfn [(bring-to-front [seq el] (cons el (remove #{el} seq)))  ;; pull the element from the list and add it to the front
          (next-nums [current remaining]  ;; bring all the elements that are in the rules to the front
            (reduce (fn [coll el] (bring-to-front coll el)) current remaining))]
    (loop [nums nums
           idx (quot (count nums) 2)]
      (let [n (first nums)
            remaining (rest nums)]
        (cond
          (and (zero? idx) (not-any? #(contains? (rules n) %) remaining)) n
          (some #(contains? (rules n) %) remaining)
          (recur (next-nums nums (filter #(contains? (rules n) %) remaining)) idx)
          :else (recur remaining (dec idx)))))))

(defn part1 [rules updates]
  (let [middle-element (fn [coll] (nth coll (quot (count coll) 2)))]
    (->> updates
         (filter #(valid-order? % rules))
         (map middle-element)
         (reduce +))))

(defn part2 [rules updates]
  (->> updates
       (filter #(not (valid-order? % rules)))
       (map (partial find-middle-element-p2 rules))
       (reduce +)))

(defn -main
  []
  (let [{:keys [rules updates]} (parse-input "resources/input.txt")]
    (time (println "Part 1: " (part1 rules updates)))
    (time (println "Part 2: " (part2 rules updates)))))