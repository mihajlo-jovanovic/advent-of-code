(ns day13.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))
        track-map (into {}
                        (mapcat (fn [[y line]]
                                  (map-indexed (fn [x c] {[x y] c}) line))
                                (map-indexed vector lines)))
        cart-chars #{\v \^ \> \<}
        carts (filter #(contains? cart-chars (val %)) track-map)]
    {:track-map (reduce (fn [m [k v]] (assoc m k (case (str v) "v" \| ">" \- "^" \| "<" \-))) track-map carts)
     :carts (reduce (fn [m [idx [pos shape]]]
                      (assoc m pos {:pos pos :dir (case (str shape) "v" :down "^" :up ">" :right "<" :left) :cnt 0 :id idx}))
                    {}
                    (map-indexed vector carts))}))

(defn calc-direction [current-dir cnt]
  (let [turn (case (mod cnt 3) 0 :left 1 :straight 2 :right)]
    (case [current-dir turn]
      [:right :left]     :up
      [:right :straight] :right
      [:right :right]    :down

      [:down :left]      :right
      [:down :straight]  :down
      [:down :right]     :left

      [:left :left]      :down
      [:left :straight]  :left
      [:left :right]     :up

      [:up :left]        :left
      [:up :straight]    :up
      [:up :right]       :right)))

(defn tick [track-map {:keys [dir cnt pos id]}]
  (let [new-pos (case dir :right (update pos 0 inc) :down (update pos 1 inc) :up (update pos 1 dec) :left (update pos 0 dec))
        on-map (track-map new-pos)]
    (case (str on-map)
      "+" {:dir (calc-direction dir cnt) :cnt (inc cnt) :pos new-pos :id id}
      "\\" {:dir (case dir :right :down :left :up :up :left :down :right) :cnt cnt :pos new-pos :id id}
      "/" {:dir (case dir :right :up :left :down :up :right :down :left) :cnt cnt :pos new-pos :id id}
      {:dir dir :cnt cnt :pos new-pos :id id})))

(defn tick-all
  "Using reduce to correctly handle simulation steps and immediate removal on crash"
  [track-map carts]
  (reduce
   (fn [carts pos]
     (if-let [cart (get carts pos)]
       (let [new-cart (tick track-map cart)
             new-pos (:pos new-cart)]
         (if (get carts new-pos)
           (dissoc carts pos new-pos)
           (-> carts (dissoc pos) (assoc new-pos new-cart))))
       carts))
   carts
   (sort (keys carts))))

(defn p1 [{:keys [track-map carts]}]
  (loop [carts carts]
    (let [first-crash
          (reduce
           (fn [carts pos]
             (if-let [cart (get carts pos)]
               (let [new-cart (tick track-map cart)
                     new-pos (:pos new-cart)]
                 (if (get carts new-pos)
                   (reduced new-pos)
                   (-> carts (dissoc pos) (assoc new-pos new-cart))))
               carts))
           carts
           (sort (keys carts)))]
      (if (vector? first-crash)
        (str/join "," first-crash)
        (recur first-crash)))))

(defn p2-reduce [{:keys [track-map carts]}]
  (loop [carts carts]
    (if (<= (count carts) 1)
      (:pos (val (first carts)))
      (recur (tick-all track-map carts)))))

(defn -main []
  (let [input (parse-input "resources/day13.txt")]
    (println "Part 1: " (time (p1 input)))
    (println "Part 2: " (time (p2-reduce input)))))

