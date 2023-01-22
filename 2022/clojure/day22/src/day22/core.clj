(ns day22.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str])
  (:gen-class))

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

(defn wrap [board {:keys [x y facing] :as pos}]
  "Wraps to the first position around the cube, regardless of how far off the edge pos is"
  (case facing
    :v (wrap-down board pos)
    :< (wrap-left board pos)
    :up (wrap-up board pos)
    :> (wrap-right board pos)))

(defn wrap-up [board {:keys [x y facing] :as pos}]
  (cond
    (<= 0 y 49) {:x (+ 50 y) :y 50 :facing :>}
    (<= 50 y 99) {:x (+ 100 y) :y 0 :facing :>}
    (<= 100 y 149) {:x 199 :y (- y 100) :facing :up}))

(defn wrap-down [board {:keys [x y facing] :as pos}]
  (cond
    (<= 0 y 49) {:x 0 :y (+ 100 y) :facing :v}
    (<= 50 y 99) {:x (+ 100 y) :y 49 :facing :<}
    (<= 100 y 149) {:x (- y 50) :y 99 :facing :<}))

(defn wrap-left [board {:keys [x y facing] :as pos}]
  (cond
    (<= 0 x 49) {:x (- 149 x) :y 0 :facing :>}
    (<= 50 x 99) {:x 100 :y (- x 50) :facing :v}
    (<= 100 x 149) {:x (- 149 x) :y 50 :facing :>}
    (<= 150 x 199) {:x 0 :y (- x 100) :facing :v}))

(defn wrap-right [board {:keys [x y facing] :as pos}]
  (cond
    (<= 0 x 49) {:x (- 149 x) :y 99 :facing :<}
    (<= 50 x 99) {:x 49 :y (+ x 50) :facing :up}
    (<= 100 x 149) {:x (- 149 x) :y 149 :facing :<}
    (<= 150 x 199) {:x 149 :y (- x 100) :facing :up}))

(defn get-ln [board {:keys [x y facing]}]
  (if (or (= facing :>) (= facing :<)) ((:rows board) x) ((:cols board) y)))

(defn wall-blocks-after-wrap? [board pos]
  "Returns True is first pos after wrapping is a wall, False otherwise"
  (let [after-wrap-pos (wrap2 board pos)
        new-direction (:facing after-wrap-pos)
        first-pos (if (or (= new-direction :>) (= new-direction :<)) (:y after-wrap-pos) (:x after-wrap-pos))]
    (contains? (:walls (get-ln board after-wrap-pos)) first-pos)))

(defn move2 [board {:keys [x y facing] :as pos} num-tiles]
  (let [ln (get-ln board pos)
        steps (case facing
                :> (map #(+ y %) (range 1 (inc num-tiles)))
                :< (map #(- y %) (range 1 (inc num-tiles)))
                :v (map #(+ x %) (range 1 (inc num-tiles)))
                :up (map #(- x %) (range 1 (inc num-tiles))))
        wall (filter #(contains? (:walls ln) %) steps)]
    (if (empty? wall)
      (let [l (+ (:offset ln) (:len ln))]
        (case facing
          :> (if (>= (+ y num-tiles) l)
               (if (wall-blocks-after-wrap? board pos)
                 {:x x :y (dec l) :facing facing}
                 (move2 board (wrap2 board pos) (- num-tiles (- l y))))
               {:x x :y (+ y num-tiles) :facing facing})
          :< (if (< (- y num-tiles) (:offset ln))
               (if (wall-blocks-after-wrap? board pos)
                 {:x x :y (:offset ln) :facing facing}
                 (move2 board (wrap2 board pos) (- num-tiles (inc (- y (:offset ln))))))
               {:x x :y (- y num-tiles) :facing facing})
          :v (if (>= (+ x num-tiles) l)
               (if (wall-blocks-after-wrap? board pos)
                 {:x (dec l) :y y :facing facing}
                 (move2 board (wrap2 board pos) (- num-tiles (- l x))))
               {:x (+ x num-tiles) :y y :facing facing})
          :up (if (< (- x num-tiles) (:offset ln))
                (if (wall-blocks-after-wrap? board pos)
                  {:x (:offset ln) :y y :facing facing}
                  (move2 board (wrap2 board pos) (- num-tiles (inc (- x (:offset ln))))))
                {:x (- x num-tiles) :y y :facing facing})))
      (case facing
        :> {:x x :y (dec (first wall)) :facing facing}
        :< {:x x :y (inc (first wall)) :facing facing}
        :v {:x (dec (first wall)) :y y :facing facing}
        :up {:x (inc (first wall)) :y y :facing facing}))))

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

(defn solve [op {:keys [board path]}]
  (let [turns (map first (drop 1 (str/split path #"\d+")))
        num-tiles (map parse-long (str/split path #"R|L"))
        init-pos {:x 0 :y (:offset (first (:rows board))) :facing :>}]
    (loop [pos (op board init-pos (first num-tiles))
           t turns
           n (rest num-tiles)]
      (if (empty? n)
        pos
        (recur (op board (turn pos (first t)) (first n)) (rest t) (rest n))))))

(defn -main [& _]
  (let [input (parse-input "day22.txt")
        solve1 (partial solve move)
        solve2 (partial solve move2)]
    (doseq [solve [solve1 solve2]]
      (println (score (solve input))))))