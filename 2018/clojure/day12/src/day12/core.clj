(ns day12.core
  (:gen-class)
  (:require
   [clojure.string :as s]))

(defn string-to-bits [s]
  (mapv #(if (= % \#) 1 0) s))

(defn bits-to-string [coll]
  (s/join (map #(if (= % 1) \# \.) coll)))

(defn next-generation [rules-map state]
  (let [padded (concat [0 0] state [0 0])]
    (mapv #(rules-map %) (partition 5 1 padded))))

(defn part1 [rules-map init-state]
  (let [step (partial next-generation rules-map)
        generations (take 21 (iterate step init-state))
        final-gen (last generations)
        indices (range -10 119)]  ;; padding 10 empy spaces on each side
    (->> (map vector final-gen indices)
         (filter (fn [[bit _]] (= bit 1)))
         (map second)
         (reduce +))))

(defn part2 [rules-map init-state]
  (doseq [g (take 100 (iterate #(next-generation rules-map %) init-state))]
    (println (bits-to-string g))))

(def left-pad 10)
(def right-pad 20)
(def part2-start 49999999913) ;; calculated offset manually based on visualization
(def part2-len 186)

(defn -main []
  (let [[init-section rules-section] (s/split (slurp "resources/input.txt") #"\n\n")
        [_ init-state] (s/split init-section #": ")
        rules (s/split-lines rules-section)
        rules-map (into {}
                        (map (fn [rule]
                               (let [[pattern result] (s/split rule #" => ")]
                                 [(string-to-bits pattern) (if (= result "#") 1 0)]))
                             rules))
        init-state-vc (vec (concat (repeat left-pad 0)
                                   (string-to-bits init-state)
                                   (repeat right-pad 0)))]
    (println "Part 1: " (time (part1 rules-map init-state-vc)))
    (println "Part 2: "
             (reduce + (range part2-start (+ part2-start part2-len))))))