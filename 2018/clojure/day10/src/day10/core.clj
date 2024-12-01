(ns day10.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (let [parse-line (fn [line] (let [pattern #"position=<\s*(-?\d+),\s*(-?\d+)> velocity=<\s*(-?\d+),\s*(-?\d+)>"
                                    [_ x y vx vy] (re-matches pattern line)]
                                {:position [(Integer/parseInt x) (Integer/parseInt y)]
                                 :velocity [(Integer/parseInt vx) (Integer/parseInt vy)]}))]
    (set (map parse-line lines))))

;; Used to calculate when the points are aligned - looking for minimum area
(defn calc-area [points]
  (let [positions (map :position points)
        min-x (apply min (map first positions))
        max-x (apply max (map first positions))
        min-y (apply min (map second positions))
        max-y (apply max (map second positions))]
    (* (- max-x min-x) (- max-y min-y))))

(defn update-points
  "Updates the positions of points based on their velocities."
  [points]
  (map (fn [{:keys [position velocity]}]
         {:position (mapv + position velocity)
          :velocity velocity})
       points))

(defn display-points
  "Displays the points as a 2D ASCII grid."
  [points]
  (let [positions (map :position points)
        min-x (apply min (map first positions))
        max-x (apply max (map first positions))
        min-y (apply min (map second positions))
        max-y (apply max (map second positions))
        position-set (set positions)]
    (doseq [y (range min-y (inc max-y))]
      (println
       (apply str
              (for [x (range min-x (inc max-x))]
                (if (position-set [x y]) "#" ".")))))))

(defn simulate-pattern
  "Simulates the movement of points, updating their positions and displaying them
   at each iteration. Stops after the given number of iterations or when manually interrupted."
  [points iterations pause-ms]
  (loop [current-points points
         step 0]
    (when (< step iterations)
      (println "\033[H\033[2J") ;; Clear the screen (ANSI escape code)
      (println "Step:" step)
      (display-points current-points)
      (Thread/sleep pause-ms)
      (recur (update-points current-points) (inc step)))))

(defn strict-update-points
  "Strictly evaluates updated points to avoid lazy accumulation."
  [points]
  (doall
   (map (fn [{:keys [position velocity]}]
          {:position (mapv + position velocity)
           :velocity velocity})
        points)))

(defn iterate-steps
  "Iteratively updates points for the given number of steps."
  [points steps]
  (loop [current-points points
         remaining-steps steps]
    (if (zero? remaining-steps)
      current-points
      (recur (strict-update-points current-points) (dec remaining-steps)))))

(defn -main []
  (let [points (parse-input (str/split-lines (slurp "resources/input.txt")))
        fast-forward (iterate-steps points 10304)]
    (display-points fast-forward)))

