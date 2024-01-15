(ns day14.core
  (:gen-class)
  (:require [clojure.string :as string]))

(defn parse-input
  "Parse multi-line string input into a vector of maps representing each point in the grid"
  [input]
  (let [parse-line (fn [[y line]] (->> line
                                       (map-indexed vector)
                                       (filter #(not (= \. (second %))))
                                       (map #(assoc {} :x (first %) :y y :value (second %)))))]
    (->> input
         (string/split-lines)
         (map-indexed vector)
         (map parse-line)
         flatten
         set)))

(defn convert-input
  [input]
  (let [grid-sz (inc (apply max (map :x input)))]
    (assoc {} :round (->> input
                          (filter #(= \O (:value %)))
                          (mapv #(vector (:x %) (- grid-sz (:y %)))))
           :cube-shaped (->> input
                             (filter #(= \# (:value %)))
                             (mapv #(vector (:x %) (- grid-sz (:y %))))))))

(defn get-by-col
  [col {:keys [round cube-shaped]}]
  (assoc {} :round (filter #(= col (first %)) round)
         :cube-shaped (filter #(= col (first %)) cube-shaped)))

(defn get-by-row
  [row {:keys [round cube-shaped]}]
  (assoc {} :round (filter #(= row (second %)) round)
         :cube-shaped (filter #(= row (second %)) cube-shaped)))

(defn tilt-north-by-col
  "Tilt north by column index"
  [rocks grid-sz col]
  (let [{:keys [round cube-shaped]} (get-by-col col rocks)
        round-y (map second round)
        first-or-zero (fn [f] (if (empty? f) 0 (first f)))]
    (loop [cube-shaped-y (reverse (sort (map second cube-shaped)))
           mx grid-sz
           mn (first-or-zero cube-shaped-y)
           acc []]
      (let [num-of-round (count (filter #(<= mn % mx) round-y))
            new-y (mapv #(vector col %) (range mx (- mx num-of-round) -1))
            acc-new  (if (empty? new-y) acc (concat acc new-y))]
        (if (empty? cube-shaped-y)
          acc-new
          (recur (rest cube-shaped-y) (dec mn) (first-or-zero (rest cube-shaped-y)) acc-new))))))

(defn tilt-west-by-row
  "Tilt west by row index"
  [rocks grid-sz row]
  (let [{:keys [round cube-shaped]} (get-by-row row rocks)
        round-x (map first round)
        first-or-n (fn [f n] (if (empty? f) n (first f)))]
    (loop [cube-shaped-x (sort (map first cube-shaped))
           mx (first-or-n cube-shaped-x grid-sz)
           mn 0
           acc []]
      (let [num-of-round (count (filter #(<= mn % mx) round-x))
            new-x (mapv #(vector % row) (range mn (+ mn num-of-round)))
            acc-new  (if (empty? new-x) acc (concat acc new-x))]
        (if (empty? cube-shaped-x)
          acc-new
          (recur (rest cube-shaped-x) (first-or-n (rest cube-shaped-x) grid-sz) (inc mx) acc-new))))))

(defn tilt-south-by-col
  "Tilt south by column index"
  [rocks grid-sz col]
  (let [{:keys [round cube-shaped]} (get-by-col col rocks)
        round-y (map second round)
        first-or-n (fn [f n] (if (empty? f) n (first f)))]
    (loop [cube-shaped-y (sort (map second cube-shaped))
           mx (first-or-n cube-shaped-y grid-sz)
           mn 1
           acc []]
      (let [num-of-round (count (filter #(<= mn % mx) round-y))
            new-y (mapv #(vector col %) (range mn (+ mn num-of-round)))
            acc-new  (if (empty? new-y) acc (concat acc new-y))]
        (if (empty? cube-shaped-y)
          acc-new
          (recur (rest cube-shaped-y) (first-or-n (rest cube-shaped-y) grid-sz) (inc mx) acc-new))))))

(defn tilt-east-by-row
  "Tilt east by row index"
  [rocks grid-sz row]
  (let [{:keys [round cube-shaped]} (get-by-row row rocks)
        round-x (map first round)
        first-or-zero (fn [f] (if (empty? f) 0 (first f)))]
    (loop [cube-shaped-x (reverse (sort (map first cube-shaped)))
           mx (dec grid-sz)
           mn (first-or-zero cube-shaped-x)
           acc []]
      (let [num-of-round (count (filter #(<= mn % mx) round-x))
            new-x (mapv #(vector % row) (range mx (- mx num-of-round) -1))
            acc-new  (if (empty? new-x) acc (concat acc new-x))]
        (if (empty? cube-shaped-x)
          acc-new
          (recur (rest cube-shaped-x) (dec mn) (first-or-zero (rest cube-shaped-x)) acc-new))))))

(defn tilt
  [f east-west? {:keys [cube-shaped] :as rocks}]
  (let [grid-sz (inc (apply max (map first cube-shaped)))
        r (if east-west? (range 1 (inc grid-sz)) (range grid-sz))]
    {:round (into [] (apply concat (map (partial f rocks grid-sz) r))) :cube-shaped cube-shaped}))

(def tilt-north (partial tilt tilt-north-by-col false))
(def tilt-west (partial tilt tilt-west-by-row true))
(def tilt-south (partial tilt tilt-south-by-col false))
(def tilt-east (partial tilt tilt-east-by-row true))

(defn cycle-button
  [rocks]
  (let [rocks-north (tilt-north rocks)
        rocks-west (tilt-west rocks-north)
        rocks-south (tilt-south rocks-west)
        rocks-east (tilt-east rocks-south)]
    rocks-east))

(defn first-duplicate [coll]
  (reduce (fn [seen x]
            (if (contains? seen x)
              (reduced x)
              (conj seen x)))
          #{} coll))

(defn part2
  [rocks]
  (let [num-of-cycles 1000000000
        offset-fn (fn [i n] (+ i (- num-of-cycles (+ (* (dec (/ (- num-of-cycles (mod num-of-cycles n)) n)) n) i))))
        cycle-seq (map :round (take 180 (iterate cycle-button rocks)))     ;; 180 came out of a REPL session
        repeated-state (first-duplicate cycle-seq)
        [i1 i2] (map first (filter #(= repeated-state (second %)) (map-indexed vector cycle-seq)))
        offset (offset-fn i1 (- i2 i1))]
    (reduce + (map second (nth cycle-seq offset)))))