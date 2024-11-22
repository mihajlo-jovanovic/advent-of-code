(ns day24.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (->> filename
       slurp
       (s/split-lines)
       (map #(s/split % #"/"))
       (map (fn [s] (map #(Integer/parseInt %) s)))))

(defn get-parts [components c]
  (into [] (filter (fn [x] (or (= c (first x)) (= c (second x)))) components)))

(defn dfs-p1
  "Modified version of DFS that deals with cycles, uses each component only once, and returns max cost path"
  [components start]
  (loop [stack (vector (vector start components 0))
         result 0]
    (if (empty? stack)
      result
      (let [[current components cost] (peek stack)
            neighbors (get-parts components current)]
        (if (empty? neighbors)
          (recur (pop stack) (max result cost))
          (let [new-stack (into (pop stack) (map
                                             (fn [[p1 p2]]
                                               (let [open-port (if (= current p1) p2 p1)
                                                     new-cost (+ cost current open-port)]
                                                 (vector open-port (filter #(not= [p1 p2] %) components) new-cost))) neighbors))]
            (recur new-stack result)))))))

;; part2 
(defn dfs-p2
  "Modified version of DFS that deals with cycles, uses each component ony once, are returns max cost path"
  [components start]
  (loop [stack (vector (vector start components 0 0))
         result 0
         l 0]
    (if (empty? stack)
      result
      (let [[current components cost length] (peek stack)
            neighbors (get-parts components current)]
        (if (empty? neighbors)
          (let [new-result (cond (> length l) cost (< length l) result :else (max result cost))
                new-length (if (> length l) length l)]
            (recur (pop stack) new-result new-length))
          (let [new-stack (into (pop stack) (map
                                             (fn [[p1 p2]]
                                               (let [open-port (if (= current p1) p2 p1)
                                                     new-cost (+ cost current open-port)]
                                                 (vector open-port (filter #(not= [p1 p2] %) components) new-cost (inc length)))) neighbors))]
            (recur new-stack result l)))))))

(defn -main []
  (let [input (parse-input "resources/input.txt")]
    (time (println "Part 1:" (dfs-p1 input 0)))
    (time (println "Part 2:" (dfs-p2 input 0)))))