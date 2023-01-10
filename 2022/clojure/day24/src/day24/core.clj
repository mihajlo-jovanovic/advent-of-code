(ns day24.core
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as str])
  (:gen-class))

(defn parse-line [[x s]]
  "Helper function for parse-blizzards - parse a single line starting with index"
  (let [l (->> s
               drop-last
               (drop 1)
               (map-indexed vector)
               (filter #(not= (second %) \.)))
        xform (map (fn [n] {:y (first n) :d (last n)}))
        xf (into [] xform l)]
    (map #(assoc % :x x) xf)))

(defn parse-blizzards [s]
  "Returns a map with keys containing blizzard coordinares, as well max x & y values"
  (let [lines (->> s
                   str/split-lines
                   drop-last
                   (drop 1))
        max-x (count lines)
        max-y (- (count (first lines)) 2)]
    {:grid  (->> lines
                 (map-indexed vector)
                 (map parse-line)
                 (filter not-empty)
                 flatten)
     :max-x max-x
     :max-y max-y}))

(defn move [b max-x max-y]
  (case (:d b)
    \> (update b :y #(mod (inc %) max-y))
    \< (update b :y #(mod (dec %) max-y))
    \v (update b :x #(mod (inc %) max-x))
    \^ (update b :x #(mod (dec %) max-x))))
(def start-pos {:x -1 :y 0})

(defn goal [state]
  {:x (dec (:max-x state)) :y (dec (:max-y state))})

(defn possible-moves [pos max-x max-y]
  "Calculates valid moves for the expedition"
  (->> [(update pos :x inc) (update pos :x dec) (update pos :y inc) (update pos :y dec)]
       (filter #(and (>= (:x %) 0) (>= (:y %) 0) (< (:x %) max-x) (< (:y %) max-y)))))

(defn valid? [pos grid]
  (not-any? #(and (= (:x pos) (:x %)) (= (:y pos) (:y %))) grid))

(defn next-states [{:keys [e grid max-x max-y] :as state}]
  "Returns a set of valid states representing possible moves from a single one"
  (let [after-blizzard (into (vector) (map #(move % max-x max-y) grid))
        pos (filter #(valid? % after-blizzard) (cons e (possible-moves e max-x max-y)))]
    (->> pos
         (map (fn [p] {:e p :grid after-blizzard :max-x max-x :max-y max-y}))
         set)))

(defn manhattan-distance [x y]
  (+ (Math/abs (- (:x x) (:x y))) (Math/abs (- (:y x) (:y y)))))
(defn keep-best-states [goal states]
  (->> states
       (sort-by #(manhattan-distance goal (:e %)))
       (take 100)))

(defn next-minute [states goal]
  (->> states
       (map next-states)
       (reduce set/union)
       (keep-best-states goal)))

(defn part1 [init-state goal]
  (loop [states (next-minute #{init-state} goal)
         min 1]
    (if (contains? (set (map :e states)) goal)
      {:min (inc min) :grid (:grid (first (filter #(= goal (:e %)) states)))}
      (recur (next-minute states goal) (inc min))))
  )

(defn solve1 [init-state]
  (:min (part1 init-state (goal init-state))))

(defn solve2 [init-state]
  (let [start start-pos
        end {:x (dec (:max-x init-state)) :y (dec (:max-y init-state))}
        first-trip (part1 init-state end)
        back (part1 (assoc (assoc init-state :grid (:grid first-trip)) :e {:x (inc (:x end)) :y (:y end)}) {:x (inc (:x start)) :y (:y start)})
        to-goal-again (part1 (assoc init-state :grid (:grid back)) end)
        ]
    (+ (:min first-trip) (dec (:min back)) (dec (:min to-goal-again)))))

(defn -main [& _]
  (let [blizzards-map (parse-blizzards (->> "day24.txt"
                                            io/resource
                                            slurp))
        init-state (merge {:e start-pos} blizzards-map)]
    (doseq [solve [solve1 solve2]]
      (println (time (solve init-state))))))