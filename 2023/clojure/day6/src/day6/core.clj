(ns day6.core
  (:gen-class))

(defn distance
  "Calculates distance traveled based on how many seconds button was held as well as how long the race lasts"
  [hold-secs time]
  (* hold-secs (- time hold-secs)))

(defn solve
  "Calculates how many ways to win for a single race"
  [time record-distance]
  (count (filter #(> % record-distance) (->> (range 0 time) (map #(distance % time))))))