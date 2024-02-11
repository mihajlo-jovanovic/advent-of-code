(ns day23.core
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as string]))

(defn parse-input
  "Parses only the paths (.) from the input and returns a set of coordinates."
  [input]
  (let [parse-line (fn [[y l]] (map #(vector (first %) y) (filter (fn [[_ c]] (= c \.)) (map-indexed vector l))))]
    (->> input
         string/split-lines
         (map-indexed vector)
         (map parse-line)
         (apply concat)
         set)))

(defn parse-input-p2
  "Same as above but includes the slopes as well for part 2."
  [input]
  (let [parse-line (fn [[y l]] (map #(vector (first %) y) (filter (fn [[_ c]] (not= c \#)) (map-indexed vector l))))]
    (->> input
         string/split-lines
         (map-indexed vector)
         (map parse-line)
         (apply concat)
         set)))

(defn parse-slopes
  "Parses only the slopes (<>V^) from the input and returns a map of coordinates to slopes."
  [input]
  (let [parse-line (fn [[y l]] (map #(assoc {} (vector (first %) y) (second %)) (filter (fn [[_ c]] (and (not= c \.) (not= c \#))) (map-indexed vector l))))]
    (->> input
         string/split-lines
         (map-indexed vector)
         (map parse-line)
         (apply concat)
         (apply merge))))

(defn neighbors [paths [x y]]
  (let [d [[-1 0] [1 0] [0 -1] [0 1]]]
    (filter #(contains? paths %) (map (fn [[dx dy]] [(+ x dx) (+ y dy)]) d))))

(defn valid? [m [x1 y1] [x2 y2]]
  (case (m [x2 y2])
    \< (and (> x1 x2) (= y1 y2))
    \> (and (< x1 x2) (= y1 y2))
    \^ (and (= x1 x2) (> y1 y2))
    \v (and (= x1 x2) (< y1 y2))
    nil false))

(defn neighbors2 [paths slopes-map [x y]]
  (let [d [[-1 0] [1 0] [0 -1] [0 1]]
        candidates (map (fn [[dx dy]] [(+ x dx) (+ y dy)]) d)
        slopes (into #{} (keys slopes-map))
        path (filter #(contains? paths %) candidates)
        slope (filter #(and (contains? slopes %) (valid? slopes-map [x y] %)) candidates)]
    (lazy-cat path slope)))

(defn walk-path
  "Walks a path from start as long as there are no crossroads - more than one possible next step.
   Returns position of last oath visited along with a set of visited coordinates."
  [paths start visited]
  (loop [pos start
         visited visited]
    (let [visited-new (conj visited pos)
          n (neighbors paths pos)
          n (filter #(not (contains? visited-new %)) n)
          count-of-n (count n)]
      (if (not= count-of-n 1)
        [pos visited]
        (recur (first n) visited-new)))))

(defn p1 [input slopes start end]
  (let [n-fn (partial neighbors2 input slopes)]
    (loop [stack (vector (vector start #{} 0))
           result -1]
      (if (empty? stack)
        result
        (let [[node visited i] (peek stack)
              visited-new (into #{} (filter #(contains? visited %) (n-fn node)))
              [next visited-next] (walk-path input node visited-new)
              neighbors (filter #(not (contains? (set/union visited visited-next) %)) (filter #(or (contains? input %) (get slopes %)) (n-fn next)))
              i-new (+ i (count visited-next))
              vis-2  (set/union visited visited-next #{next})
              new-stack (into (pop stack) (map #(vector % vis-2 i-new) neighbors))]
          ;; (println "node: " node "next: " next "visited-next: " visited-next "neighbors: " neighbors "new-stack: " new-stack "i-new: " i-new "result: " result)
        ;; (if (and (= end next) (> i-new result))
        ;;   (println "found: " i-new))
          (if (and (= end next) (> i-new result))
            (recur new-stack i-new)
            (recur new-stack result)))))))

;; PART 2

(defn helper [input v]
  (let [neighbors (neighbors input v)
        visited (conj #{} v)
        paths (map #(walk-path input % visited) neighbors)]
    (map (fn [[p seen]] (vector p v (count seen))) paths)))

(defn build-reduced-graph
  "Builds a graph with only the decision points and their neighbors."
  [input]
  (let [decision-points (filter #(> (count (neighbors input %)) 2) input)]
    (apply concat (map #(helper input %) decision-points))))

(defn successors [g v]
  (merge (apply merge (map (fn [[_ e w]] (assoc {} e w)) (filter #(= (first %) v) g)))
         (apply merge (map (fn [[e _ w]] (assoc {} e w)) (filter #(= (second %) v) g)))))

;; (defn dfs [g f start end]
;;   (loop [stack (vector (vector start #{} 0))
;;          result -1]
;;     (if (empty? stack)
;;       result
;;       (let [[curr seen d] (peek stack)
;;             successors (filter (fn [[k _]] (not (contains? seen k))) (f g curr))
;;             seen-new (conj seen curr)
;;             new-stack (into (pop stack) (map (fn [[k v]] (vector k seen-new (+ d v))) successors))]
;;         (if (and (= end curr) (> d result))
;;           (println "found: " d))
;;         (if (and (= end curr) (> d result))
;;           (recur new-stack d)
;;           (recur new-stack result))))))

(defn manhattan [p1 p2]
  (apply + (map (fn [[x1 x2]] (Math/abs (- x1 x2))) (map vector p1 p2))))

(defn dfs-2
  "Modified DFS to keep track of visited nodes and remember longest path encounted; added a heuristic
   based on manhattan distance that probably does nothing."
  [g f start end]
  (loop [stack (vector (vector start #{} 0))
         result -1]
    (if (empty? stack)
      result
      (let [[curr seen d] (peek stack)
            successors (filter (fn [[k _]] (not (contains? seen k))) (f g curr))
            seen-new (conj seen curr)
            nx (sort-by (fn [[k _]] (- (manhattan end k))) successors)
            new-stack (into (pop stack) (map (fn [[k v]] (vector k seen-new (+ d v))) nx))]
        (if (and (= end curr) (> d result))
          (println "found: " d))
        (if (and (= end curr) (> d result))
          (recur new-stack d)
          (recur new-stack result))))))

(defn -main []
  (let [input (slurp "resources/day23.txt")
        paths (parse-input-p2 input)
        start [1 0]]
    (time (println (dfs-2 (build-reduced-graph paths) successors start [139 140])))))

;;  [1 0] 
;   /  \   (15)
;  /    \
;[4 5]   [3 6]

;; FAILED ATTEMPS BELLOW THIS POINT
;; ********************************

;; (def start [1 0])
;; (def end [21 22])
;; (def end [139 140])

;; (defn p1 [paths slopes start i visited]
;;   ;; (println "visited: " start)
;;   (if (= start end)
;;     i
;;     (let [n (neighbors2 paths slopes start)
;;           n (filter #(not (contains? visited %)) n)]
;;       (if (empty? n)
;;         -1
;;         (case (count n)
;;           4 (max (p1 paths slopes (first n) (inc i) (conj visited start))
;;                  (p1 paths slopes (second n) (inc i) (conj visited start))
;;                  (p1 paths slopes (nth n 2) (inc i) (conj visited start))
;;                  (p1 paths slopes (nth n 3) (inc i) (conj visited start)))
;;           3 (max (p1 paths slopes (first n) (inc i) (conj visited start))
;;                  (p1 paths slopes (second n) (inc i) (conj visited start))
;;                  (p1 paths slopes (nth n 2) (inc i) (conj visited start)))
;;           2 (max (p1 paths slopes (first n) (inc i) (conj visited start))
;;                  (p1 paths slopes (second n) (inc i) (conj visited start)))
;;           1 (p1 paths slopes (first n) (inc i) (conj visited start)))))))

          ;; (recur (first n) (inc i) (conj visited pos))

;; (defn p2 [paths start end]
;;   (loop [pos start
;;          i 0
;;          visited #{}
;;          acc []]
;;     (println "acc: " acc)
;;     (if (= pos end)
;;       i
;;       (let [n (neighbors pos)
;;             n (filter #(and (contains? paths %) (not (contains? visited %))) n)
;;             next (if (= 1 (count n)) acc (concat acc (map #(assoc {} % i) (rest n))))]
;;         (if (empty? n)
;;           (recur (first (keys (first acc))) (first (vals (first acc))) (conj visited pos) (rest acc))
;;           (recur (first n) (inc i) (conj visited pos) next))))))

;; (defn follow-path [paths {:keys [pos visited] :as node}]
;;   (loop [pos pos
;;          i 0
;;          visited visited]
;;     ;; (println "pos: " pos "visited: " visited)
;;     (if (= pos end)
;;       (assoc {} {:pos end :visited (conj visited pos)} i)
;;       (let [n (neighbors pos)
;;             n (filter #(and (contains? paths %) (not (contains? visited %))) n)]
;;         (if (empty? n)
;;           nil
;;           (if (> (count n) 1)
;;             (apply merge (map #(assoc {} {:pos % :visited (conj visited pos)} (inc i)) n))
;;             (recur (first n) (inc i) (conj visited pos))))))))

;; (def paths (parse-input-p2 (slurp "resources/input.txt")))

;; (def input (parse-input (slurp "resources/input.txt")))
;; (def slopes (parse-slopes (slurp "resources/input.txt")))
;; (def n-fn (partial neighbors2 input slopes))

;; (def walk-path
;;   (memoize
;;    (fn [start visited]
;;      (loop [pos start
;;             visited visited]
;;        (let [visited-new (conj visited pos)
;;              n (n-fn pos)
;;              n (filter #(and (contains? input %) (not (contains? visited-new %))) n)
;;              count-of-n (count n)]
;;          (if (not= count-of-n 1)
;;            [pos visited]
;;            (recur (first n) visited-new)))))))

;; (defn walk-path
;;   "Walks a path from start as long as there are no crossroads - more than one possible next step.
;;    Returns position of last oath visited along with a set of visited coordinates."
;;   [paths f start visited]
;;   (loop [pos start
;;          visited visited]
;;     (let [visited-new (conj visited pos)
;;           n (f pos)
;;           n (filter #(and (contains? paths %) (not (contains? visited-new %))) n)
;;           count-of-n (count n)]
;;       (if (not= count-of-n 1)
;;         [pos visited]
;;         (recur (first n) visited-new)))))

;; (defn p2-heuristic [paths slopes start end]
;;   (apply max (for [[s1 s2 s3 s4] (combo/combinations (filter #(not= % [3 4]) (keys slopes)) 4)]
;;                (let [slopes-new (dissoc slopes s1 s2 s3)
;;                      paths-new (conj (conj (conj (conj paths s1) s2) s3) s4)
;;                      result (p1 paths-new slopes-new start end)]
;;                  result))))

;; (defn p2 [input start end]
;;   (loop [stack (vector (vector start #{} 0))
;;          result -1]
;;     (if (empty? stack)
;;       result
;;       (let [[node visited i] (peek stack)
;;             visited-new (into #{} (filter #(contains? visited %) (filter #(contains? input %) (neighbors node))))
;;             [next visited-next] (walk-path input node visited-new)
;;             neighbors (filter #(not (contains? (set/union visited visited-next) %)) (filter #(contains? input %) (neighbors next)))
;;             i-new (+ i (count visited-next))
;;             vis-2  (set/union visited visited-next #{next})
;;             new-stack (into (pop stack) (map #(vector % vis-2 i-new) neighbors))]
;;         (println "node: " node "next: " next "visited-next: " visited-next "neighbors: " neighbors "new-stack: " new-stack "i-new: " i-new "result: " result)
;;         ;; (if (and (= end next) (> i-new result))
;;         ;;   (println "found: " i-new))
;;         (if (and (= end next) (> i-new result))
;;           (recur new-stack i-new)
;;           (recur new-stack result))))))