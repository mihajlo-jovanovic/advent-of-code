(ns day4.core
  (:gen-class)
  (:require [clojure.string :as s]))

(def state {:guard-id nil :sleep-start nil :sleep-end nil})

(def guard-stats (atom {}))

(defn process-line [state line]
  (let [[_ _ time action] (re-matches #"\[(\d{4}-\d{2}-\d{2}) \d{2}:(\d{2})\] (.+)" line)]
    (cond
      (= action "falls asleep") (assoc state :sleep-start time)
      (= action "wakes up")
      (do (swap! guard-stats assoc (state :guard-id)
                 (concat (get @guard-stats (state :guard-id)) (range (Integer/parseInt (state :sleep-start)) (Integer/parseInt time)))) state)
      :else (let [[_ guard-id] (re-matches #"Guard #(\d+) begins shift" action)]
              (assoc state :guard-id guard-id)))))

(defn -main []
  (reduce process-line state (s/split-lines (slurp "resources/input_sorted.txt")))
  (let [guard (apply max-key (comp count val) @guard-stats)
        max-freq (apply max (map val (frequencies (val guard))))
        most-frequent-min (key (first (filter (fn [[_ v]] (= v max-freq)) (frequencies (val guard)))))
        max-freq-p2 (apply max (flatten (map vals (map second (map (fn [[k v]] [k (frequencies v)]) @guard-stats)))))
        guard-p2 (first (filter (fn [[_ v]] (contains? (into #{} (map val v)) max-freq-p2)) (map (fn [[k v]] [k (frequencies v)]) @guard-stats)))
        most-frequent-min-p2 (first (first (filter (fn [[_ v]] (= v max-freq-p2)) (second guard-p2))))]
    (println "Part 1: " (* (Integer/parseInt (key guard)) most-frequent-min))
    (println "Part 2: " (* (Integer/parseInt (first guard-p2)) most-frequent-min-p2))))