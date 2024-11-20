(ns day19.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-grid [file-path]
  (let [lines (->> (slurp file-path)
                   str/split-lines
                   (remove str/blank?))]
    (into {}
          (for [[y line] (map-indexed vector lines)
                [x char] (map-indexed vector line)
                :when (not (Character/isWhitespace char))]
            [[x y] char]))))

(defn change-direction [grid state max-x max-y]
  (let [[x y] (:pos state)]
    (case (:dir state)
      (:down :up)
      (let [next-pos [(inc x) y]]
        (if (and (contains? grid next-pos) (< x max-x))
          (assoc state :dir :right :pos next-pos :path (str (:path state) \+))
          (assoc state :dir :left :pos [(dec x) y] :path (str (:path state) \+))))
      (:left :right)
      (let [next-pos [x (inc y)]]
        (if (and (contains? grid next-pos) (< y max-y))
          (assoc state :dir :down :pos next-pos :path (str (:path state) \+))
          (assoc state :dir :up :pos [x (dec y)] :path (str (:path state) \+)))))))

(defn calculate-next-pos [[x y] dir]
  (case dir
    :down [x (inc y)]
    :up [x (dec y)]
    :left [(dec x) y]
    :right [(inc x) y]))

(defn within-bounds? [[x y] max-x max-y]
  (and (<= 0 x max-x) (<= 0 y max-y)))

(defn step [grid state max-x max-y]
  (let [current-pos (:pos state)
        ch (get grid current-pos)]
    (if (= \+ ch)
      (change-direction grid state max-x max-y)
      (let [next-pos (calculate-next-pos current-pos (:dir state))]
        (if (and (contains? grid next-pos) (within-bounds? next-pos max-x max-y))
          (assoc state :pos next-pos :path (str (:path state) ch))
          (assoc state :pos next-pos :path (str (:path state) ch) :end? true))))))

(defn follow-path [grid state]
  (loop [state state]
    (let [new-state (step grid state 199 199)]
      (if (:end? new-state)
        new-state
        (recur new-state)))))

(defn -main []
  (let [grid (parse-grid "resources/input.txt")
        state {:pos [75 0] :dir :down :path ""}]
    (println "Part 1: " (->>
                         (follow-path grid state)
                         :path
                         (filter #(Character/isLetter %))
                         (apply str)))
    (println "Part 2: " (->>
                         (follow-path grid state)
                         :path
                         count))))