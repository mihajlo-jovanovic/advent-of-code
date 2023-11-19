(ns day3.core
  (:gen-class))

(def input 361527)

(defn level
  [pos]
  (max (abs (:x pos)) (abs (:y pos))))

(defn move [direction pos]
  (let [{:keys [x y]} pos]
    (case direction
      :right {:x (inc x), :y y}
      :left {:x (dec x), :y y}
      :top {:x x, :y (inc y)}
      :bottom {:x x, :y (dec y)})))

(defn valid?
  [f pos]
  (= (level pos) (level (f pos))))

(defn bottom-right-corner?
  [pos]
  (and (= (:x pos) (level pos))
       (= (:y pos) (- (level pos)))))

(defn spiral-coords
  ([] (spiral-coords {:x 0 :y 0}))
  ([c]
   (let [next-coordinate (fn [c]
                           (if (bottom-right-corner? c)
                             (move :right c)
                             (cond
                               (and (= (level c) (:x c)) (valid? (partial move :top) c)) (move :top c)
                               (and (= (level c) (:y c)) (valid? (partial move :left) c)) (move :left c)
                               (valid? (partial move :bottom) c) (move :bottom c)
                               :else (move :right c))))]
     (lazy-seq (cons c (spiral-coords (next-coordinate c)))))))

; write a function neighbors? that checks if two positions are neighbors including diagonals
(defn neighbors?
  [p1 p2]
  (let [dx (Math/abs (- (:x p1) (:x p2)))
        dy (Math/abs (- (:y p1) (:y p2)))]
    (and (<= dx 1) (<= dy 1) (not (and (= dx 0) (= dy 0))))))

(defn get-value
  [i state]
  (if (= 0 i)
    state
    (let [pos (nth (spiral-coords) (count state))
          val (reduce + (map :val (filter #(neighbors? pos %) state)))]
      (recur (dec i) (conj state (merge pos {:val val}))))))

(defn p1
  []
  (let [pos (last (take input (spiral-coords)))]
    (+ (abs (:x pos)) (abs (:y pos)))))

(defn p2
  []
  (let [start {:x 0 :y 0 :val 1}]
    (first (filter #(> % input) (map :val (get-value 100 [start]))))))

(defn -main
  []
  (println "Part 1: " (p1))
  (println "Part 2: " (p2)))