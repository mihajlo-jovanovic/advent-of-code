(ns day13.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-map [filepath]
  (let [lines (str/split-lines (slurp filepath))
        track-map (->> (for [y (range (count lines))
                             x (range (count (nth lines y)))
                             :let [c (nth (nth lines y) x)]]
                         {[x y] c})
                       (apply merge))
        cart-shapes #{\v \^ \> \<}
        carts (filter #(contains? cart-shapes (val %)) track-map)]
    {:track-map (reduce (fn [m [k v]] (assoc m k (case (str v) "v" \| ">" \- "^" \| "<" \-))) track-map carts)
     :carts (mapv (fn [[idx [pos shape]]] {:pos pos :dir (case (str shape) "v" :down "^" :up ">" :right "<" :left) :cnt 0 :id idx}) (map-indexed vector carts))}))

(defn calc-direction [current-dir cnt]
  (let [turn (case (mod cnt 3) 0 :left 1 :straight 2 :right)]
    (case current-dir
      :right (case turn :left :up :straight :right :right :down)
      :down (case turn :left :right :straight :down :right :left)
      :left (case turn :left :down :straight :left :right :up)
      :up (case turn :left :left :straight :up :right :right))))

(defn tick [track-map {:keys [dir cnt pos id]}]
  (let [new-pos (case dir :right (update pos 0 inc) :down (update pos 1 inc) :up (update pos 1 dec) :left (update pos 0 dec))
        on-map (track-map new-pos)]
    (case (str on-map)
      "+" {:dir (calc-direction dir cnt) :cnt (inc cnt) :pos new-pos :id id}
      "\\" {:dir (case dir :right :down :left :up :up :left :down :right) :cnt cnt :pos new-pos :id id}
      "/" {:dir (case dir :right :up :left :down :up :right :down :left) :cnt cnt :pos new-pos :id id}
      {:dir dir :cnt cnt :pos new-pos :id id})))

(defn first-duplicate
  "Returns first duplicate element in a collection, otherwise returns a set containing the entire coll"
  [coll]
  (reduce (fn [acc x]
            (if (contains? acc x)
              (reduced x)
              (conj acc x)))
          #{} coll))

(defn p1 [{:keys [track-map carts]}]
  (let [step-fn (fn [m] (map (partial tick track-map) m))]
    (str/join "," (first-duplicate (map :pos (map (partial tick track-map) (last (take-while #(let [positions (map :pos %)] (= (count (set positions)) (count positions))) (iterate step-fn carts)))))))))

(defn p2
  "This does not work: it processes a tick instantaneously withou regard to specific instructions in the problem statement about order"
  [{:keys [track-map carts]}]
  (let [step-fn (fn [m] (map (partial tick track-map) m))
        crashed? (fn [positions cart] (> (count (filter #(= (:pos cart) %) positions)) 1))]
    (loop [carts carts]
      (if (= 1 (count carts))
        (str/join "," (:pos (first carts)))
        (let [carts-before-crash (last (take-while #(let [positions (map :pos %)] (= (count (set positions)) (count positions))) (iterate step-fn carts)))
              carts-after-crash (map (partial tick track-map) carts-before-crash)
              crash-pos (first-duplicate (map :pos carts-after-crash))
              remaining-carts (filter (complement (partial crashed? (map :pos carts-after-crash))) carts-after-crash)]
          (recur remaining-carts))))))

(defn step2
  "Using reduce to make sure order of position is honored"
  [track-map carts]
  (let [in-move-order? (fn [c1 c2] (let [[x1 y1] (:pos c1) [x2 y2] (:pos c2)] (and (>= x2 x1) (>= y2 y1))))
        res
        (reduce
         (fn [[acc crashed] c]
           (if (contains? crashed (:id c))
             [acc crashed]
             (let [c-new (tick track-map c)
                   same-pos-as-c-new #(= (:pos %) (:pos c-new))]
               (if (and (some same-pos-as-c-new carts) (in-move-order? c c-new))
                 [acc (conj crashed (:id (first (filter same-pos-as-c-new carts))))]
                 (if (some same-pos-as-c-new acc)
                   [(remove same-pos-as-c-new acc) crashed]
                   [(conj acc c-new) crashed])))))
         [[] #{}] (sort-by :pos carts))]
    (first res)))

(defn p2-reduce [{:keys [track-map carts]}]
  (let [step-fn (partial step2 track-map)]
    (:pos (first (step-fn (last (take-while #(> (count %) 1) (iterate step-fn carts))))))))

(defn -main []
  (let [input (parse-map "resources/day13.txt")]
    (println "Part 2: " (time (p2-reduce input)))))
