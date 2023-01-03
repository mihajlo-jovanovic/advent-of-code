(ns day20.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(def start-state
  {:encrypted [1 2 -3 3 -2 0 4]
   :pos       (into (vector) (range 0 7))
   :idx       0})

(defn new-idx [idx val len]
  "Calculates new index position based on old one, value and length of the file"
  (let [pos (mod (+ idx val) (dec len))]
    (if (and (= 0 pos) (neg? val))
      (dec len)
      pos))
  )

(defn shift [coll pred? op]
  "Applies op function to every element of the collection coll matching given predicate"
  (into (vector) (map #(if (pred? %) (op %) %) coll)))

(defn mix [{:keys [encrypted pos idx] :as state}]
  (let [len (count pos)
        new-i (new-idx (pos idx) (encrypted idx) len)
        in-range? (fn [a b] #(< a % b))]
    (if (>= new-i (pos idx))                                ;; shifting right
      {:encrypted encrypted
       :pos       (assoc (shift pos (in-range? (pos idx) (inc new-i)) dec) idx new-i)
       :idx       (mod (inc idx) len)}
      {:encrypted encrypted                                 ;; shifting left
       :pos       (assoc (shift pos (in-range? (dec new-i) (pos idx)) inc) idx new-i)
       :idx       (mod (inc idx) len)})))

(defn helper [state]
  "Sum up 1000, 2000, 3000 element after 0, given final state map"
  (let [len (count (:pos state))
        offset ((:pos state) (.indexOf (:encrypted state) 0))]
    (->> (map #(+ offset %) [1000 2000 3000])
         (map #(mod % len))
         (map #(.indexOf (:pos state) %))
         (map #((:encrypted state) %))
         (apply +))))

(defn solve1 [init-state]
  (let [num-iterations (inc (count (:pos init-state)))]
    (helper (last (take num-iterations (iterate mix init-state))))))

(defn solve2 [init-state]
  (let [num-iterations (inc (* 10 (count (:pos init-state))))
        state-p2 (update init-state :encrypted #(into (vector) (map (partial * 811589153) %)))]
    (helper (last (take num-iterations (iterate mix state-p2))))))

;(map first (sort-by second (partition 2 (interleave (:encrypted res) (:pos res)))))

(defn -main [& _]
  (let [input (-> "day20.txt"
                  io/resource
                  slurp
                  str/split-lines)
        state {:encrypted (into (vector) (map parse-long input))
               :pos       (into (vector) (range 0 (count input)))
               :idx       0}]
    (doseq [solve [solve1 solve2]]
      (println (time (solve state))))))