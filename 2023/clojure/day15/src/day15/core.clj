(ns day15.core
  (:require [clojure.string :as string]))

(defn hash [s]
  (reduce (fn [h c]
            (mod (* 17 (+ (int c) h)) 256))
          0
          s))

(defn find-index [vector match-fn]
  (loop [index 0
         remaining-vector vector]
    (if (empty? remaining-vector)
      nil
      (let [item (first remaining-vector)]
        (if (match-fn item)
          index
          (recur (inc index) (rest remaining-vector)))))))

(defn combine
  [m s]
  (if (string/includes? s "=")
    (let [parts (string/split s #"=")
          label (first parts)
          focal-length (Integer/parseInt (second parts))
          h (hash label)]
      (if (contains? m h)
        (let [fnd (filter #(= label (first %)) (get m h))]
          (if (empty? fnd)
            (assoc m h (conj (get m h) [label focal-length]))
            (let [slots (get m h)
                  idx (find-index slots #(= label (first %)))]
              (assoc m h (update (get m h) idx #(vector (% 0) focal-length))))))
        (assoc m h [[label focal-length]])))
    (let [label (subs s 0 (dec (count s)))
          h (hash label)]
      (if (contains? m h)
        (assoc m h (into [] (filter #(not= label (first %)) (get m h))))
        m))))

(defn calculate-focusing-power [lenses]
  (let [focusing-power-for-lens (fn [[box lenses]]
                                  (reduce +
                                          (map-indexed (fn [idx [_ focal]]
                                                         (* (+ 1 box) (inc idx) focal))
                                                       lenses)))]
    (reduce +
            (map focusing-power-for-lens lenses))))

(defn part2
  [lenses]
  (->> (string/split lenses #",")
       (map string/trim)
       (reduce combine (hash-map))
       calculate-focusing-power))