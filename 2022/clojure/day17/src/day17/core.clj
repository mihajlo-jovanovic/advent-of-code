(ns day17.core
  (:require [clojure.set :as set])
  (:gen-class))

;;copied from input
(def ^:const jet-pattern ">>><<<<>>><<<<><<<<>><><>>>><<><<>>>><<>>>><<<>>>><<<>>><<<<>><<><>>><><<>><<>><<><>>><>>>><<<><<<>>><<<<>><<<<>>>><<>>>><<>><<<<>><><<<><<<<>>><<<<>>>><<<>>><<<>>><>><<><<<<>>>><<<><>>>><<<<>>><<><<<<>><<<<>>>><><<<<>>>><<<<>><<<<><<<>>>><<<><<<>><<>>>><<<>><<<<>>>><<<>>>><>><<<<>>><<><>>><>>>><<>>>><<>>>><>>>><>><<>><<><>>>><<>>><<>>>><<<<>>>><>><>>>><>>>><<<>><<>>>><<<<><<<<><<<>><<<>>><<<<><<<<>>><<<>><<<>>><<<<>>><<<>><<<>>>><>>>><<<>>>><<<<>><<>>><<<>>>><<>>>><<<<>>><<<<><>>>><>>>><<>>>><>>><<<<>>><<<>>><<<<><>><<<<>><<>>><<>>><>>>><<<<>>><<>>>><>>>><>><<<<>>>><<<<>>><<<>>>><<>><<<<>><<>>><>>>><<<>>>><>>><<<>>>><>><<<<>>><>><<<><<<<>>>><>>>><>><<>><<><<><><><<<<>>>><><<>>><>><<><<<>>>><<<>><<<<>>><<<<><<<>>>><><<>>><>>><<<<>><<<>>><<<<><<<<><<<>><><><<<>>><>><><<<<>><<<><><<>>><<<>>><<<<>><<<<>><<<>><>>>><><<<<>>>><<<<>><<<<><<>><<<>>>><><<>>>><<><<><>><<<<>><>><<<><<<>>><<>><<<<>>><<<<>><<<<><<<><><>>>><<<<>>><<<>>><<<>>><<<>>>><><>>>><>><<<<><>>><<<>><<<<><<<>>>><<<>>><<<<>>>><<<>>>><<<<>><>>><<<>>>><>>><>>>><<><>><<<<><<<<><><<><<>>>><<<>><<<<><<<>>><<<<>>><<>><<<>>>><><<<>>>><<<>>>><<>>><<<>><<<<>><<<<>><<><<<>><<>>><<<>>><<>>><<<<><><<>>><>><<<>>>><<<>><<<>>><<><<<><<<>>>><<<<>><<<<>>>><<>><<<<>><<<>>><<<>>><<>>><<<>>><><>><<<<>>>><<<>>>><<<<>><<<<>>>><>>><<><>>>><<>>><<<>>><>>><<<<>><>><<>>><<<<>>><<<>>><<>><<<<>>>><<<<><>><<<<>><<>><<<>>><<>>><<<<>><<>><<>>>><<><<<<>><<<<>><<<<><<>><<<<>>>><<<><<<<>>><<>>>><>>>><<>>><<<><<><<>><<><<<<>>>><>>><><<><<<<><<>>><><<<<>>>><>>><<<<><<<<>>><>><<<>>>><<<<>>>><<><<>>><<>>><>><<<<>>>><<>>>><>>><<<><<<<><<><<><<>><<<<>><<>>>><<<>>><>><<<<>>>><<<<><>>><>>><<<>>><><>>>><<<><<>>><<<<>><<<>>>><>>><<<><<>>>><<<>>>><<<<>>>><<>>><<>><<<<><<<<>>><><<<<>>><<<>><<>>><>>>><<<>><<<>><<>>><<><<<<>><<<>>>><<>>><<<><>>><<<<>>><>>><<>><<>><<<>><<<<>>>><><<<<>>><<<<><<>><<<>>><<<>>><><<>><<<<>>>><<<>><<<<>>><<<>>>><<<>>><<>>>><><<>>>><<<><<<>><>><>>>><<><<>>><>>>><<<<>>><<<<>>><<<>>>><<<>><<<<><<>>>><>>><<>>><<><<<<>>>><<><<<>><<<<><<<><<<>>><>>>><<<<>><<<<>>>><<<<>>>><<<>>>><>>><>>><<<>>>><<<>><><<<<>><<<>>>><<<>>>><><>><<<<>><<<>>>><<><<<><<<<>>>><<>>><<>>>><>><>>><<<<><<><<<>><<<<><<><<<<><>><>><<<>>><><<<<><>>><<<<><<<>>>><<<><<<<>>>><<<>><<>><<<<>>>><<<>><<>>><>>>><<<<><>><<<><<<>><<<>><>>><<<<>><<>><<>>><>><<>><<<><<>>><<<<><<>>>><>>>><<<>><<<>>>><<<<>><><<<>>>><>>><<<<><><<<>>><<>><<>>><>>>><<<>>><<>>>><<<<>>>><<<>><>><>>>><<>>><><<>><<<<>>>><>>>><<<>>><<<<><<<<>><>>>><<<>>><<>>>><<<<>>><<<><>>><<<<>><<<<>>><<<><<<>>>><<<<><>>><<>>><<<>>><<<<>><>>><<>>>><<>>>><<<<>>><<>><<<>>>><<<>>><<>>>><>>><<<><<<><<>><>>>><<<<><>>>><<<<>>>><<<>><<<<>>><>>>><<<>>>><<<<>>>><<>><<<>><>>>><<<<>>><<<<>>><>>>><<><<<>>><<<<>>>><<<<>><>><<<<>>>><>>><<>>>><<<><<<>>>><<<<><<<<>>>><<<>>>><<<>><>><>>>><>>>><>><<<>>><<>>><><<<>><<>>>><<<>>><>>><<<>><<><<<><<<><<<>><>>>><<<<><<<<>>><<>><<<>><<<>><<>><>>>><<<<>>>><<<>><<<<><<>>><<<>>>><><<<<><<<>><<><<>>><<<<><>>><<<<>>><<<>>>><>>><<>>>><<<<>>><><<>>><>><<<<>>>><>>><>><<><<<<>>><<><<<><<>>><<<<>><<<<>>>><<><<>>>><<<<>>>><<>><<>><<<<>>>><><<>><<>>><<<>>>><>>>><>>>><<><>>><<<<>>><<<<>>>><<<>>><<<>>><<>><<<>>><<<<><>>><<<>>>><<<>>><<<<>>><<<<>><<<>>>><>>>><><<<>>>><<>><>>><<>><<>>><<<>>>><<<><<>><<>>>><<<<>>>><<<>>><<<<>>><>>><<>>><<<<><<<>><<>>>><<>><<><><<>>>><<<><<<>><<<<>>>><>>><<<>>><><>><<>>><<<<>>><<>>>><<<>><>>>><<>>><<>>>><<<<>>><<<>>><>><<<<><>><><<>>><<>>><>>><<<>><<>><<<<>><<>>><<<<><>>><><<<>><>><>>><<<<>>>><<><<>><<<<>><<<><><<>><>>>><<>>><>>><>>>><>>><<<>>>><<<>>><<>>>><<<<>>>><<<>><<<<>>><><<>><<><<><<>>><<<>><<<<>>><<>><<<>>>><>>><<>><>>>><>><<<>>>><<<<>>>><<<>>><<<<>><>>><<><<>>>><<<<><<<<>><>>><<<<>>>><<>>>><<>><<<>><<<<>><<><<<<><><>>><<<>>><<<><>>><<<>>>><<<>><<<<>><>>>><<<>>><>>><<<<>><>>><<>>>><<<<>>>><>><<><<><<<><<<<>>>><<<<><<<<><<<<>>>><<>><<>>>><<<<><<<>><<<>>>><<><>>>><<<<>>>><<<>><>>><<<><><>><<><<><<<<><<<<><<>>><>>>><<<<>>>><<<><<<<>><>><<><<>>><<<><<>><<<>><<><<<<><<<>><<>>><<>>><<>>><<<<>>><<<>>><<<>>>><<<<>><<<<>>>><<>>>><<<>>><<<<>>>><<>>><<<><<>>><<<><><>>><<>><<<>>>><<<>>><>>>><<<>><<<<>>>><<<<><<<<>>><>><<<>>><>>>><<<<>>><<<>>><<<>><>>>><<<<>><<<<>>>><<<<>>><>>>><<<>>><<<>>><<<>>>><<><<>>>><<<>>>><<<<>><><><<<<>>>><<>>><<><<>><<<<>>><<<>>><<>>><><<<>>>><>><>>><<<>>><<<<><<<>><><<<><<>><<<<>>>><<>>><>>><<<<><>>><<<<>>><<<<>><<<>>>><<<<>><<>>>><<<><<>>>><<>><<<<>>><<<>>><<<<>>>><<<>>>><<<>><>>><<>><<<<>>>><<>><<<<>><>>>><<<<>>>><>>><<<<>>><>>>><<<<>>><<<>>>><<<>>>><><<<<>>><<>>>><>><<<>><<<<>>>><<<<>>>><<<<>>>><<>>><<>>>><<<>><<>><<>>><<<<>><<>>>><<>>><<<<>><<<>><<<<>>>><<<>>>><>>><>>><<>>>><<>>><<<<>>><<><<<<><<>><<><>><<<>>><><<<>>><<<<>>>><>><>>><>>><>>>><<<>><<>>>><>>><<<<>>><><<>>><>>>><>><>>>><<<<><<<<><<>>><<<<><>>>><><><<<>>>><>>>><<<<>><<><<<>>>><<<>><>><<>><<>>><<<>><<>>>><<<>>><<<>>><<<<>>><<><>>><<<><<<>>><<<>><<<>>><<<>><><>><<<>>><<<>>><<<<>>>><<<>>>><<<<>>>><<<<>>>><<<<>>>><<>>><>>>><<>>>><<<>><>>><>>>><<<>>>><<<>>><>>>><>><<<><<<<>>><>><<>><<<<>>>><<<<>><<<<>>><<<<>><<<<>>>><><<<<>>>><>>><><<<<>><<<<>>><>>>><<<<><>>><<<><<<>>><<<<><>><<>><<>>>><>><<<><<<><<<>>>><>><<<><<<>>><<><<<><<<<>><>>>><<>>><<<<>>>><><>>>><>>>><<<<><<>>>><<<>>><<<<>>><>>><<>><<>><>><<<<>>>><<<><<<>><>>>><><<<>><<<<>><>>>><<<>>>><>>>><>><<<>>><>>><<>>><<<<>>><<<<><<>>><<>>>><<<>>>><<<<>><<<>>>><<>><<>><><<><<<<>>>><>>>><>><<<<><<><>>>><<>><<<>><<<>>>><>>><<<>>>><<><<<<><<<>>><>>>><>>>><>>>><<>><><<><<>>>><<<<><<<><><<<<>>><<><<>>>><<>>><><<<<><<<><>>>><<>>><<<<><<<><>>>><<<<>><<><>><<><>>>><<<<><<<<><>>><<<>>>><<<>><<<>><>><<>><<>>>><>>>><>>><>><<><<>>>><<><<<><<><>>><<<<>>>><<<<>><<<<>>><>>>><<<<>>><<<<><><<<<><<><<<><<><<>>><<<<>>>><<<><<<><>>><<<<><<>>><><<<>>>><<<>><<>>>><<<<>>>><<<>>>><<>><<>>><>>><<<<><<<>>>><><<>><<>><>>><<<>>><<>><<>><>>><<<<>><<<>><<>>><<<<>>><<>>><<<>>>><<><<><<<>>>><<>>>><<><<>>><<<><<<>>>><>>>><<<>>>><<<>><<<>><>>><<>><<<>>>><<><<<>><>><<<>>>><<>>>><>>>><<>>>><<<><>>>><<><>>><<>>><<><<<<>>><>>><<<<><><><<<<>>>><<<>>><<>>><<<>><<<<><><<>>><<<<>>><<<>>><>>>><<<<><<><>><<<><<<>>><>><<<<><<>><<>>><>>>><>>><<><<<<><<<<>><<<>>><><<<<>>><<<<>><<<<><<<<><>>>><<<<><<<<>><<<<>><<<<>>><<>>><<>>>><<<><<<><<>>><<>>><<<<><<<<>><<<<>><<<>>>><<<<>>>><<<>>>><<<<>>>><<>><><<><>>><<<<>>>><<<>>><>><<<<>>><<<<>>><><>><><<<>><<><<<<><>>><<<><<><<<>>><<<<>>><>>>><>>>><<<<>>>><<<<>>><<<<>>><<<><>><>>><<<<><<>>>><>><<<<>>>><<<<>>><<>><<><<<><<<<>><<<<>><><>>>><<<><<<<>>>><<>>>><<<<>><<<>><<>>>><<<<>>>><<<<>>><<<>>>><<<>>><>>>><<<<>>><<<>>><><<<>>>><<>>>><<<<>>>><<<<>><<<>>><<>>><<><<>><>>><<<<>><>><<<<>><<<<>><<>>>><<><<<<>>><<>>>><<<<>>><><<<<><<>>>><<>><>><>>>><<<>>>><<>><>>>><<>>>><<<<>>><<<<><>><>>>><>>>><<<>>><>>>><<<><<<<>>>><<>><<>>><<<><<<>>><<<<>>>><<<<><<>>>><>>>><>><<<<>>><<<<>><<>>>><><<<<>>><<<<><<<>><<>>><<>>><<<>>><<><<><<<<><<>>>><<<<><><<<<>>>><>>><<<>>><<<<><<<<>>>><<<<>>><<<>><><<><><><>>><<<>>><>><<>>><<<>>><<<>>><<><<<><<<>>>><>>><>><<>>><<<<><>>>><<<>><><<<>>>><><<<><>><<>><<<<><<<<><>><><>>>><<><>>>><<>>><<><<<>>><<<<>>><<<<>>>><<<<>><<<>>><>><<<><<>><<<<>>><<<>>>><<<>>><<<>>><<<<><<<><<<>><<<>><<<<>>>><<<<><<<>><<>><<<>>><<<>>><><<<>>><<<>>>><<><<<<>>>><<>>><<<>>><<><>>><<>><<><<<<>>>><<<<><<<>><>><>><>>><<<<>><>>>><<<><<>>>><<<<>>><<<<>><>>><<<>>><>><><<<>>>><<<<>>><<>>>><<><>><<<><>>><<<<>><<<><<><<<>><<>><<<<>><<<<><<>>><><<>>><<<<>>>><<<>><>>><<<<>><<<<>>>><<<<><<<>><>>>><<<<>><<<>>><<<>>>><>><<>>><<><>><<<><<<><>>>><<<<>><>><<<>>>><<>>>><>><<<>>><<<>>>><<<>>>><<<<>>>><<<<>><>>><>><<<<><<<<><<<<>><<<<>>><<<>><<<<>>><<<><<<<><<<<>>>><<><><>>>><<>><>>>><<<<>>>><<<<>>>><<<><<>><>><<<>>>><<<<>><<<>>>><>>><<<>><<<<>><<<<>>><<>>><<<<>>><<>>><<<<>>>><<>>>><<>>><<><<>><><>><<>>>><<>>>><<<<>>>><<<<>>>><<>><<>><<<<>>>><<>>>><<>>>><<<>>>><<<<>>>><<<>>>><<><>>><<<>>>><>>><>><<<<>><<>>><<<>>><<<<><<>>>><<<<>>>><><<<>>>><>>>><>><<<>>>><<<<>>><>><<>>><<<<><<><>><>><<>>><<<<><<<<>><<<>><>><<<<>>>><<<>><<>><>><<<<>>>><<<<>><>><<<<><><<<><<<<>><<>>><>><<>>><>>><<<<>>>><>>><<<>>><<<<>><>>>><>>>><<>>>><<<<>>><>><>>><<<>><<<<>><<>><<<<><<><>><<<<><<><>><>>>><<<<><<<>><<<<>>><<>>>><<<<>>>><<>>>><<<<>>>><<<<><<<>>>><><<<<>>><<<>>>><<>><>>><<<><>>>><<<<>>>><<>><<>>>><<<>>>><>><>><<><<>><<<<>>><<<>><><<<>>><>>>><<<<>>><<<<><<<>>>><<>><<<><<>>>><<<<>>><<><<><<>>>><<<<><<><<><<>>><><><<<<><><>><<>>>><>>>><<<>>>><<>>>><<>>>><<<>>><>>>><><<<>><<>><<<><<<<>>>><>>><>><<>>>><><<>><<>><<><<><>><<<<>>>><<<<>>>><>><><<<><<>>><<>>><<<>>><<<<>>><<>><<<<>>><>>><>>>><<>><<<<>>><<<<>><<<><<>>><<<<><<<<>>><<><><<<>><<>><<<><>><>>>><>>><<>>>><>><<>>><<>>>><<<<><><<<>><<<>><<<>><<<<>><>>><<>>>><>>><<<>><>>>><<<<>>>><<<><<><<<>><<<<>><<<<>>>><<>>>><<><>><<>>>><<<>>><<>>>><<>>><<><<<>>><>>><<><<>>>><<<<>><<<><<<<>>><<<<>>><<>>><>><<>>>><>>><>>>><<<<><><<>>><>><<<>>><><<>>><<<>>><>>>><<<>><<<>><<<<>><<>><>>>><<><>>><><<<<>><<<<>>><<><<>><<<>><<<<><<>><<><<<>>>><<<>>><<>>>><<>>>><<><<<>><><<<>>><<<>><>>><<><>><<>>><<<<>><<>><<>><<>>>><<>>><<>>><<>><<<>><<<<><><<<<><>>><><<<<><>><<<>><<<<><>>><<<>>><>><>>><<<<>>>><<<<><><><<<>><><<<<>>>><>>>><<>><<<>><<<<>><><<<><<>><<<>><<<>>>><<<<>>><<<<>>>><<<>><><<<<><<<<><<>><<<>>><<>>>><<>>>><<<>>><<>>><<>><<<>>><<><<>>><<<<><<>><>>><<<><<<<>><<>>><>>><<>><>>>><<<<>>><><<<><<<<><<<<>>>><<<<><<<<>>>><<>>>><<<><<<<>><<><<<<><<<>>><<>><<<<>><<>><><<<>><<>>>><<>><<<>>>><>>>><<<<><>>><>>><<><<<><>>>><<>>><<<<>><<>><<<<><>>><<>><>>>><<<>>>><<<>>><>>><<<>>>><<<<><<><<<<>>>><<<><<><>>>><<<>><<<>>>><<<<>><<<>>><<<<>><<<>>>><<<>><<><<<>>><<<<><<<<>>><<<<><<<>><<<<>><<<<>>>><<<>><<<>>>><<<<>><<>>>><<><<<>><>><>>><<<<><<<>><<<<><>>><<>>>><<>>><<<<>>>><<>>><>><<<<>>><<>><<<>>>><><<<<>>><<<<><<>><<<<><>>>><<<<>>>><<<<>>>><<>>><<<>>><<<<>>><>><<>><<<>>><><<<<>>>><>>>><>>><<<<>>>><<<<><<><<<<>><<>><<<>>>><<>><<<><<><<><<><>>>><<<<><<<><<<<>>><<>><<>><<<>>><<<>>>><>>><<>><<<<>><<<<><<><<<<>><<>>><<<<>>><<<<>>>><<<>><<<><<>>><>>><>><><<<<><<<>>><<<>><<<><<><<>>><<>>>><<>>><>>>><<<><<<<>><>>><<>>>><<>>><><<<<>>><>><<<>><<<><<<<>>><<<>>><><<<>>><<<>><<<<>>><<<<>>><><<>><<<>>>><<<<><<>>><<>>>><<<>><>><<><<>><<>>>><<<<><><<>><>>><>>>><<<>>><<<<><><<<<>><>>><>><<<<><>>><<<>>><<<>>><<>><<>>><<>>>><<>><<<>>><<<>><<")

;(def ^:const jet-pattern ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")

(defn line [pos]
  (let [x (:x pos)
        y (:y pos)]
    (into (hash-set) [pos {:x (inc x) :y y} {:x (+ 2 x) :y y} {:x (+ 3 x) :y y}])))

(defn cross [pos]
  (let [x (:x pos)
        y (:y pos)]
    (into (hash-set) [{:x (inc x) :y y} {:x (inc x) :y (inc y)} {:x (inc x) :y (+ 2 y)} {:x x :y (inc y)} {:x (+ 2 x) :y (inc y)}])))

(defn box [pos]
  (let [x (:x pos)
        y (:y pos)]
    (into (hash-set) [pos {:x (inc x) :y y} {:x (inc x) :y (inc y)} {:x x :y (inc y)}])))

(defn l [pos]
  (let [x (:x pos)
        y (:y pos)]
    (into (hash-set) [pos {:x (inc x) :y y} {:x (+ 2 x) :y y} {:x (+ 2 x) :y (inc y)} {:x (+ 2 x) :y (+ 2 y)}])))

(defn down [pos]
  (let [x (:x pos)
        y (:y pos)]
    (into (hash-set) [pos {:x x :y (inc y)} {:x x :y (+ 2 y)} {:x x :y (+ 3 y)}])))

(defn fall-rock [r op coll]
  "Helper function to move rock once left, right (due to jet stream) ir down (due to gravity)"
  (let [new-r (map #(op %) r)]
    (if (and (every? #(>= (:x %) 0) new-r) (every? #(< (:x %) 7) new-r)
             (every? #(> (:y %) 0) new-r)
             (every? #(not (contains? coll %)) new-r))
      new-r
      r)))

(defn move-left [p] {:x (dec (:x p)) :y (:y p)})
(defn move-right [p] {:x (inc (:x p)) :y (:y p)})
(defn move-down [p] {:x (:x p) :y (dec (:y p))})

(defn run-simulation-step [state]
  "Simulate one single rock falling. Returns state so can be used as iterate target"
  (let [step (:step state)
        chamber (:chamber state)
        pos {:x 2 :y (+ 4 (if (empty? chamber)
                            0
                            (apply max (map :y chamber))))}
        shape (case (mod step 5) 0 line
                                 1 cross
                                 2 l
                                 3 down
                                 4 box)]
    (loop [rock (shape pos)
           s (:step-jet state)]
      (let [jet (nth jet-pattern (mod s (count jet-pattern)))
            new-rock (if (= jet \>) (fall-rock rock move-right chamber) (fall-rock rock move-left chamber))
            new-rock2 (fall-rock new-rock move-down chamber)]
        (if (= new-rock new-rock2)
          {:step (mod (inc step) 5) :step-jet (inc s) :chamber (set/union (into (hash-set) new-rock2) chamber)}
          (recur new-rock2 (mod (inc s) (count jet-pattern))))))))

(defn p1 [n state]
  (apply max (map :y (:chamber (last (take n (iterate run-simulation-step state)))))))

(defn raise-floor [coll]
  "Simplifies chamber map by raising the floor: find highest y value for each x (0..7) then make smallest of those the
  new floor. Returns the same collection if one of the x coordinates does not exist"
  (if (< (count (distinct (map :x coll))) 7)  ;; this one gave me some trouble in sample input pattern, had to add 7 for good measure
    coll
    (let [max-y (- (apply min (map #(apply max (map :y (filter (fn [x] (= % (:x x))) coll))) (range 0 7))) 8)]
      (map #(assoc % :y (- (:y %) max-y)) (filter #(>= (:y %) max-y) coll)))))

(defn run-simulation-step-p2 [state]
  (let [step (:step state)
        chamber (:chamber state)
        pos {:x 2 :y (+ 4 (if (empty? chamber)
                            0
                            (apply max (map :y chamber))))}
        shape (case (mod step 5) 0 line
                                 1 cross
                                 2 l
                                 3 down
                                 4 box)]
    (loop [rock (shape pos)
           s (:step-jet state)]
      (let [jet (nth jet-pattern (mod s (count jet-pattern)))
            new-rock (if (= jet \>) (fall-rock rock move-right chamber) (fall-rock rock move-left chamber))
            new-rock2 (fall-rock new-rock move-down chamber)]
        (if (= new-rock new-rock2)
          {:step (mod (inc step) 5) :step-jet (inc s) :chamber (into (hash-set) (raise-floor (set/union (into (hash-set) new-rock2) chamber)))}
          (recur new-rock2 (mod (inc s) (count jet-pattern))))))))

(defn display [coll]
  (doseq [y (reverse (range 1 42))]
    (print "|")
    (doseq [x (range 0 7)]
      (if (contains? coll {:x x :y y})
        (print "#")
        (print ".")))
    (println "|"))
  (println "+-------+"))

(defn first-duplicate [coll]
  (reduce (fn [acc x]
            (if (contains? acc x)
              (reduced x)
              (conj acc x)))
          #{} coll))

(defn -main
  [& _] (time
          (let [init-state {:step 0 :step-jet 0 :chamber #{}}
                dup-state (first-duplicate (take 3000 (iterate run-simulation-step-p2 init-state)))
                dup-state-idx (map first (filter #(= dup-state (second %)) (map-indexed vector (take 3000 (iterate run-simulation-step-p2 init-state)))))
                repeat-rocks (- (second dup-state-idx) (first dup-state-idx))
                repeat-height (- (p1 (second dup-state-idx) init-state) (p1 (first dup-state-idx) init-state))
                remaining-rocks (- (+ repeat-rocks (mod 1000000000000 repeat-rocks)) (first dup-state-idx))
                rem-rocks-height (- (p1 (inc (+ remaining-rocks (first dup-state-idx))) init-state) (p1 (inc (first dup-state-idx)) init-state))
                height-at-first-repeat (p1 (inc (first dup-state-idx)) init-state)]
            (println "Part 2 Solution: " (+ height-at-first-repeat (* (dec (quot 1000000000000 repeat-rocks)) repeat-height) rem-rocks-height)))))