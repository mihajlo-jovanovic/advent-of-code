(ns day14.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.set :as set]))

(defn parse-input [input]
  (map (fn [line]
         (let [[pos-part vel-part] (map s/trim (s/split line #" "))
               pos-nums (mapv #(Integer/parseInt (s/trim %)) (s/split pos-part #","))
               vel-nums (mapv #(Integer/parseInt (s/trim %)) (s/split vel-part #","))]
           (zipmap [:px :py :vx :vy] (concat pos-nums vel-nums))))
       (s/split-lines input)))

(defn move-n-steps [n max-x max-y points]
  (map (fn [point]
         (let [px (:px point)
               py (:py point)
               vx (:vx point)
               vy (:vy point)]
           (zipmap [:px :py :vx :vy] [(mod (+ px (* vx n)) max-x) (mod (+ py (* vy n)) max-y) vx vy])))
       points))

(def update-points
  (partial move-n-steps 1 101 103))

(defn quadrant [{:keys [px py]} max-x max-y]
  (let [half-x (/ max-x 2)
        half-y (/ max-y 2)]
    (cond
      (and (< px half-x) (< py half-y)) 1
      (and (>= px half-x) (< py half-y)) 2
      (and (< px half-x) (>= py half-y)) 3
      :else 4)))

(defn neighbor? [p1 p2]
  (let [x1 (:px p1)
        y1 (:py p1)
        x2 (:px p2)
        y2 (:py p2)]
    (or (and (= x1 x2) (= y1 (inc y2)))
        (and (= x1 (inc x2)) (= y1 y2))
        (and (= x1 (dec x2)) (= y1 y2))
        (and (= x1 x2) (= y1 (dec y2))))))

(defn group-elements
  "Groups elements of `coll` into a set of sets based on `membership-fn`.
   `membership-fn` should be a function of two arguments that returns true
   if those two elements belong in the same subset."
  [membership-fn coll]
  (reduce
   (fn [acc element]
      ;; Find all subsets whose members match `element` according to membership-fn
     (let [matching-subsets (filter
                             (fn [subset]
                               (some #(membership-fn element %) subset))
                             acc)]
       (cond
          ;; No matches: we must start a new subset
         (empty? matching-subsets)
         (conj acc #{element})

          ;; One match: add element to that subset
         (= 1 (count matching-subsets))
         (let [subset-to-extend (first matching-subsets)]
           (-> (disj acc subset-to-extend)
               (conj (conj subset-to-extend element))))

          ;; Multiple matches: merge them all into one, then add the new element
         :else
         (let [merged-subset (reduce clojure.set/union
                                     #{element}
                                     matching-subsets)]
           (-> (reduce disj acc matching-subsets)
               (conj merged-subset))))))
    ;; Start with an empty set of subsets
   #{}
   coll))

;; I used this one to find the point to fast-forward to in order to find the answer to part 2
(defn max-num-of-pixels-together [input n]
  (apply max (->> (move-n-steps n 101 103 input)
                  (group-elements neighbor?)
                  (map count))))

(defn part1 [filename]
  (let [points (parse-input (slurp filename))]
    (reduce * (map #(count (val %)) (group-by #(quadrant % 101 103) (filter (complement #(or (= 50 (:px %)) (= 51 (:py %)))) (move-n-steps 100 101 103 points)))))))

(defn display-points
  "Displays the points as a 2D ASCII grid."
  [positions]
  (let [min-x (apply min (map first positions))
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
      (display-points (map #(vector (:px %) (:py %)) current-points))
      (Thread/sleep pause-ms)
      (recur (update-points current-points) (inc step)))))

(defn -main
  []
  (let [input (parse-input (slurp "resources/input.txt"))
        fast-forward (move-n-steps (+ 347 7500) 101 103 input)]
    (simulate-pattern fast-forward 1 3000)
    (println "Part 1:" (part1 "resources/input.txt"))
    (println "Part 2:" (+ 347 7500))))