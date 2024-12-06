(ns day6.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-grid
  "Parse a multi-line string into a set of {:x :y :value} maps, skipping '.' chars."
  [input]
  (let [lines (s/split-lines input)]
    (->> (for [y (range (count lines))
               x (range (count (nth lines y)))
               :let [c (nth (nth lines y) x)]
               :when (not= c \.)]
           {:x x :y y :value c})
         set)))

(defn change-direction [direction]
  (case direction
    :up     :right
    :down   :left
    :left   :up
    :right  :down))

(defn move [grid state]
  (let [{px :x py :y} state
        new-pos (case (:direction state)
                  :up    {:x px :y (dec py)}
                  :down  {:x px :y (inc py)}
                  :left  {:x (dec px) :y py}
                  :right {:x (inc px) :y py})]
    (if (some (fn [cell]
                (and (= (:x cell) (:x new-pos))
                     (= (:y cell) (:y new-pos))
                     (= (:value cell) \#)))
              grid)
      (assoc state :direction (change-direction (:direction state)))
      (assoc state :x (:x new-pos) :y (:y new-pos)))))

(defn off-grid? [{:keys [x y]} mx-size]
  (or (neg? x)
      (neg? y)
      (>= x mx-size)
      (>= y mx-size)))

(defn part1 [filename]
  (let [grid (parse-grid (slurp filename))
        mx-size (inc (max (apply max (map :x grid)) (apply max (map :y grid))))
        player-pos (first (filter #(= (:value %) \^) grid))
        start {:x (:x player-pos) :y (:y player-pos) :direction :up}]
    (count (into #{} (map (fn [m] (dissoc m :direction)) (take-while #(not (off-grid? % mx-size)) (iterate (partial move grid) start)))))))

(defn in-a-loop? [coll mx-size]
  (reduce (fn [seen x]
            (if (contains? seen x)
              (reduced true)
              (if (off-grid? x mx-size)
                (reduced false)
                (conj seen x))))
          #{} coll))

(defn part2 [filename]
  (let [grid (parse-grid (slurp filename))
        mx-size (inc (max (apply max (map :x grid)) (apply max (map :y grid))))
        player-pos (first (filter #(= (:value %) \^) grid))
        px (:x player-pos)
        py (:y player-pos)
        start {:x px :y py :direction :up}
        unique-positions (into #{} (map (fn [m] (dissoc m :direction)) (take-while #(not (off-grid? % mx-size)) (iterate (partial move grid) start))))
        unique-positions-minus-start (disj unique-positions {:x px :y py})]
    (loop [positions unique-positions-minus-start
           counter 0]
      (if (empty? positions)
        counter
        (let [new-grid (conj grid {:x (:x (first positions)) :y (:y (first positions)) :value \#})]
          (if (in-a-loop? (iterate (partial move new-grid) start) mx-size)
            (recur (rest positions) (inc counter))
            (recur (rest positions) counter)))))))

(defn -main
  []
  (time (println "Part 1: " (part1 "resources/input.txt")))
  (time (println "Part 2: " (part2 "resources/input.txt"))))