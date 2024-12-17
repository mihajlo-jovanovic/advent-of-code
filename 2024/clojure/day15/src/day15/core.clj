(ns day15.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-grid
  "Parse a multi-line string into a map with {:anthenas :size} keys, where anthenas is a map of values to list of coordinates."
  [filename]
  (let [parts (s/split (slurp filename) #"\n\n")
        lines (s/split-lines (first parts))]
    {:warehouse-map (->> (for [y (range (count lines))
                               x (range (count (nth lines y)))
                               :let [c (nth (nth lines y) x)]]
                           {c [x y]})
                         (reduce (fn [acc m]
                                   (let [[k v] (first m)]
                                     (update acc k (fnil conj #{}) v)))
                                 {})) :size (count lines)
     :moves (chars (char-array (apply concat (map s/trim-newline (s/split-lines (second parts))))))}))

(defn get-next-pos [[x y] move]
  (case move
    \^ [x (- y 1)]
    \v [x (+ y 1)]
    \< [(- x 1) y]
    \> [(+ x 1) y]))

(defn can-push-box? [walls spaces [pos-x pos-y] move]
  (case move
    \^  (let [next-free-space (map second (filter (fn [[x y]] (and (< y pos-y) (= x pos-x))) spaces))
              next-wall (apply max (map second (filter (fn [[x y]] (and (< y pos-y) (= x pos-x))) walls)))]
          (if (empty? next-free-space)
            nil
            (let [next-free-space' (apply max next-free-space)]
              (if (> next-free-space' next-wall)
                [pos-x next-free-space']
                nil))))
    \v (let [next-free-space (map second (filter (fn [[x y]] (and (> y pos-y) (= x pos-x))) spaces))
             next-wall (apply min (map second (filter (fn [[x y]] (and (> y pos-y) (= x pos-x))) walls)))]
         (if (empty? next-free-space)
           nil
           (let [next-free-space' (apply min next-free-space)]
             (if (< next-free-space' next-wall)
               [pos-x next-free-space']
               nil))))
    \< (let [next-free-space (map first (filter (fn [[x y]] (and (< x pos-x) (= y pos-y))) spaces))
             next-wall (apply max (map first (filter (fn [[x y]] (and (< x pos-x) (= y pos-y))) walls)))]
         (if (empty? next-free-space)
           nil
           (let [next-free-space' (apply max next-free-space)]
             (if (> next-free-space' next-wall)
               [next-free-space' pos-y]
               nil))))
    \> (let [next-free-space (map first (filter (fn [[x y]] (and (> x pos-x) (= y pos-y))) spaces))
             next-wall (apply min (map first (filter (fn [[x y]] (and (> x pos-x) (= y pos-y))) walls)))]
         (if (empty? next-free-space)
           nil
           (let [next-free-space' (apply min next-free-space)]
             (if (< next-free-space' next-wall)
               [next-free-space' pos-y]
               nil))))))

(defn can-push-box-p2? [walls spaces [pos-x pos-y] move]
  (case move
    \^ (let [above [pos-x (dec pos-y)]
             next-to-above [(inc pos-x) (dec pos-y)]]
         (cond (or (contains? walls above) (contains? walls next-to-above)) false
               (and (contains? spaces above) (contains? spaces next-to-above)) true
               (contains? spaces above) (can-push-box-p2? walls spaces next-to-above move)
               (contains? spaces next-to-above) (can-push-box-p2? walls spaces [(dec pos-x) (dec pos-y)] move)))))

(defn move
  "Move the robot in the direction given by the move."
  [walls {:keys [:pos :boxes :spaces :moves] :as state}]
  (if (empty? moves)
    state
    (let [next-move (first moves)
          next-pos (get-next-pos pos next-move)]
      (if (contains? walls next-pos)
        (assoc state :moves (rest moves))
        (if (contains? boxes next-pos)
          (let [free-space (can-push-box? walls spaces next-pos next-move)]
            (if (some? free-space)
              (assoc state :pos next-pos :boxes (conj (disj boxes next-pos) free-space) :spaces (conj (disj spaces free-space) pos) :moves (rest moves))
              (assoc state :moves (rest moves))))
          (assoc state :pos next-pos :spaces (conj spaces pos) :moves (rest moves)))))))

(defn display-points
  "Displays the points as a 2D ASCII grid."
  [grid size]
  (let [walls (get grid \#)
        boxes1 (get grid \[)
        boxes2 (get grid \])]
    (doseq [y (range size)]
      (println
       (apply str
              (for [x (range size)]
                (if (contains? walls [x y]) "#" (if (contains? boxes1 [x y]) "[" (if (contains? boxes2 [x y]) "]" (if (= [x y] (first (get grid \@))) "@" "."))))))))))

(defn simulate-pattern
  "Simulates the movement of points, updating their positions and displaying them
   at each iteration. Stops after the given number of iterations or when manually interrupted."
  [state walls iterations pause-ms]
  (loop [current-state state
         step 0]
    (when (< step iterations)
      (println "\033[H\033[2J") ;; Clear the screen (ANSI escape code)
      (println "Move:" (first (:moves current-state)) "  Step: " step)
      (display-points {\# walls \O (:boxes current-state) \@ #{(:pos current-state)}} 10)
      (Thread/sleep pause-ms)
      (recur (move walls current-state) (inc step)))))

(defn score [coll]
  (reduce + (map (fn [[x y]] (+ (* 100 y) x)) coll)))

(defn part1 [filename]
  (let [input (parse-grid filename)
        grid (:warehouse-map input)
        moves (:moves input)
        walls (get grid \#)
        state {:pos (first (get grid \@)) :boxes (get grid \O) :spaces (get grid \.) :moves moves}]
    (score (:boxes (last (take-while #(seq (:moves %)) (iterate (partial move walls) state)))))))

;; part 2

(defn parse-grid-p2
  "Parse a multi-line string into a map with {:anthenas :size} keys, where anthenas is a map of values to list of coordinates."
  [filename]
  (let [parts (s/split (slurp filename) #"\n\n")
        lines (s/split-lines (first parts))]
    {:warehouse-map (->> (for [y (range (count lines))
                               x (range (count (nth lines y)))
                               :let [c (nth (nth lines y) x)]]
                           (case c
                             \# [{c [(* 2 x) y]} {c [(inc (* 2 x)) y]}]
                             \O [{\[ [(* 2 x) y]} {\] [(inc (* 2 x)) y]}]
                             \@  [{c [(* 2 x) y]} {\. [(inc (* 2 x)) y]}]
                             \. [{c [(* 2 x) y]} {c [(inc (* 2 x)) y]}]))
                         (flatten)
                         (reduce (fn [acc m]
                                   (let [[k v] (first m)]
                                     (update acc k (fnil conj #{}) v)))
                                 {})) :size (count lines)
     :moves (chars (char-array (apply concat (map s/trim-newline (s/split-lines (second parts))))))}))

(defn -main
  []
  (let [input (parse-grid "resources/sample.txt")
        grid (:warehouse-map input)
        moves (:moves input)
        walls (get grid \#)
        state {:pos (first (get grid \@)) :boxes (get grid \O) :spaces (get grid \.) :moves moves}]
    (simulate-pattern state walls 70 2000)))