(ns day2.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-line [s]
  (let [parts (str/split s #"; ")]
    (mapv (fn [part]
            (let [pairs (re-seq #"\d+\s\w+" part)]
              (reduce (fn [m pair]
                        (let [[_ count color] (re-matches #"(\d+)\s(\w+)" pair)]
                          (assoc m (keyword color) (Integer/parseInt count))))
                      {}
                      pairs)))
          parts)))

(defn possible? [m]
  (let [green (get m :green 0)
        blue (get m :blue 0)
        red (get m :red 0)]
    (and (<= green 13)
         (<= blue 14)
         (<= red 12))))

(defn power [m]
  (let [min-green (apply max (filter #(not (nil? %)) (map :green m)))
        min-blue (apply max (filter #(not (nil? %)) (map :blue m)))
        min-red (apply max (filter #(not (nil? %)) (map :red m)))]
    (* min-green min-red min-blue)))

(defn parse-input [f]
  (->> f
       slurp
       clojure.string/split-lines
       (map parse-line)))

(defn p1 [input]
  (->> input
       (map-indexed vector)
       (filter #(every? possible? (second %)))
       (map (fn [x] (inc (first x))))
       (reduce +)))

(defn p2 [input]
  (->> input
       (map power)
       (reduce +)))

(defn -main
  [& _]
  (let [input (parse-input "resources/input.txt")]
    (println "Part 1: " (p1 input))
    (println "Part 2: " (p2 input))))