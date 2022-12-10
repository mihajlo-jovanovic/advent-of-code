(ns day9.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn top [a b]
  (and (= (:x a) (:x b)) (= (- (:y a) 2) (:y b))))

(defn bottom [a b]
  (and (= (:x a) (:x b)) (= (+ (:y a) 2) (:y b))))

(defn left [a b]
  (let [x1 (:x a)
        y1 (:y a)
        x2 (:x b)
        y2 (:y b)]
    (and (= y1 y2) (= (+ x1 2) x2))))

(defn right [a b]
  (let [x1 (:x a)
        y1 (:y a)
        x2 (:x b)
        y2 (:y b)]
    (and (= y1 y2) (= (- x1 2) x2))))

(defn top-left [a b]
  (and (< (:x a) (:x b)) (> (:y a) (:y b))))

(defn top-right [a b]
  (and (> (:x a) (:x b)) (> (:y a) (:y b))))

(defn bottom-left [a b]
  (and (< (:x a) (:x b)) (< (:y a) (:y b))))

(defn bottom-right [a b]
  (and (> (:x a) (:x b)) (< (:y a) (:y b))))

(defn touch? [a b]
  (and (< (abs (- (:x a) (:x b))) 2)
       (< (abs (- (:y a) (:y b))) 2)))

(defn movement [a b]
  (cond
    (touch? a b) b
    (top a b) {:x (:x b) :y (inc (:y b))}
    (left a b) {:x (dec (:x b)) :y (:y b)}
    (right a b) {:x (inc (:x b)) :y (:y b)}
    (bottom a b) {:x (:x b) :y (dec (:y b))}
    (top-left a b) {:x (dec (:x b)) :y (inc (:y b))}
    (top-right a b) {:x (inc (:x b)) :y (inc (:y b))}
    (bottom-left a b) {:x (dec (:x b)) :y (dec (:y b))}
    (bottom-right a b) {:x (inc (:x b)) :y (dec (:y b))}
    :else b))

(defn move-rope-once [dir coll]
  (let [h (first coll)
        tail (rest coll)
        nh (case dir
             \R {:x (inc (:x h)) :y (:y h)}
             \U {:x (:x h) :y (inc (:y h))}
             \L {:x (dec (:x h)) :y (:y h)}
             \D {:x (:x h) :y (dec (:y h))})]
    (reductions movement (cons nh tail))))

(defn move-rope [coll instructions acc]
  (if (empty? instructions)
    (count acc)
    (let [[i c] (first instructions)
          tmp (partial move-rope-once i)
          state (take (inc c) (iterate tmp coll))]
      (recur (last state) (rest instructions) (into acc (map last state))))))

(defn parse [s]
  (->> s
       (str/split-lines)
       (map #(str/split % #" "))
       (map #(vector (first (first %)) (Integer/parseInt (last %))))))

(defn -main
  [& _]
  (let [input (-> "day9.txt"
                  io/resource
                  str
                  slurp
                  parse)
        rope (take 10 (repeat {:x 0 :y 0}))]
    (println "Part 2 solution: " (move-rope rope input #{}))))