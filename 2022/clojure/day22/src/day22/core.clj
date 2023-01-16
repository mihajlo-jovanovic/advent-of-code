(ns day22.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn parse-row [s]
  (let [indexed (map-indexed vector s)
        tiles (map first (filter #(or (= \. (second %)) (= \# (second %))) indexed))
        offset (apply min tiles)
        len (inc (- (apply max tiles) offset))
        walls (into (sorted-set) (map first (filter #(= \# (second %)) indexed)))]
    {:offset offset :len len :walls walls}))

(defn parse-column [rows col-idx]
  (let [indexed (map-indexed vector rows)
        matches (filter #(<= (:offset (second %)) col-idx (+ (:offset (second %)) (:len (second %)))) indexed)
        offset (apply min (map first matches))
        len (inc (- (apply max (map first matches)) offset))
        walls (into (sorted-set) (map first (filter #(contains? (:walls (second %)) col-idx) matches)))]
    {:offset offset :len len :walls walls}))

(defn parse-columns [rows]
  (let [max-y (apply max (map #(+ (:offset %) (:len %)) rows))]
    (map #(parse-column rows %) (range 0 max-y))))

(defn parse-board [s]
  "Returns map of rows and cols, where each are vectors or maps containing offset, len and walls keys"
  (let [rows (->> s
                  str/split-lines
                  (map parse-row))
        cols (parse-columns rows)]
    {:rows (vec rows) :cols (vec cols)}))

(defn parse-input [f]
  (let [s (->> f
               io/resource
               slurp)
        parts (str/split s #"\n\n")]
    {:board (parse-board (first parts))
     :path  (first (str/split-lines (last parts)))}))

(defn mod-with-offset [offset len n]
  (if (< (dec offset) n (+ offset len))
    n
    (+ offset (mod (- n offset) len))))

(defn move [board {:keys [x y facing] :as pos} num-tiles]
  (let [ln (if (or (= facing :>) (= facing :<)) ((:rows board) x) ((:cols board) y))
        adj (partial mod-with-offset (:offset ln) (:len ln))
        to (case facing
             :> (adj (+ y num-tiles))
             :< (adj (- y num-tiles))
             :v (adj (+ x num-tiles))
             :up (adj (- x num-tiles)))
        steps (case facing
                :> (map #(adj (+ y %)) (range 1 (inc num-tiles)))
                :< (map #(adj (- y %)) (range 1 (inc num-tiles)))
                :v (map #(adj (+ x %)) (range 1 (inc num-tiles)))
                :up (map #(adj (- x %)) (range 1 (inc num-tiles)))
                )
        wall (filter #(contains? (:walls ln) %) steps)]
    (if (empty? wall)
      (if (or (= facing :>) (= facing :<)) {:x x :y to :facing facing} {:x to :y y :facing facing})
      (case facing
        :> {:x x :y (adj (dec (first wall))) :facing facing}
        :< {:x x :y (adj (inc (first wall))) :facing facing}
        :v {:x (adj (dec (first wall))) :y y :facing facing}
        :up {:x (adj (inc (first wall))) :y y :facing facing}))))

(defn direction [old turn]
  (case old
    :> (if (= \R turn) :v :up)
    :v (if (= \R turn) :< :>)
    :< (if (= \R turn) :up :v)
    :up (if (= \R turn) :> :<)))

(defn turn [pos dir]
  (update pos :facing #(direction % dir)))

(defn score [{:keys [x y facing]}]
  (let [f (case facing
            :> 0
            :v 1
            :< 2
            :up 3)]
    (+ (* 1000 (inc x)) (* 4 (inc y)) f)))

(defn part1 []
  (let [{:keys [board path]} (parse-input "day22.txt")
        turns (map first (drop 1 (str/split path #"\d+")))
        num-tiles (map parse-long (str/split path #"R|L"))
        init-pos {:x 0 :y (:offset (first (:rows board))) :facing :>}]
    (loop [pos (move board init-pos (first num-tiles))
           t turns
           n (rest num-tiles)]
      (if (empty? n)
        pos
        (recur (move board (turn pos (first t)) (first n)) (rest t) (rest n))))))