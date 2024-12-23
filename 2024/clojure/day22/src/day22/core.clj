(ns day22.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.set :as set]))

(defn mix [n secret-number]
  (bit-xor n secret-number))

(defn prune [secret-number]
  (mod secret-number 16777216))

(defn next-secret-number [secret-number]
  (let [tmp (* 64 secret-number)
        new-secret-number (prune (mix tmp secret-number))
        new-secret-number' (prune (mix (int (/ new-secret-number 32)) new-secret-number))
        new-secret-number'' (prune (mix (* new-secret-number' 2048) new-secret-number'))]
    new-secret-number''))

(defn part1 [nums]
  (reduce + (map #(last (take 2001 (iterate next-secret-number %))) nums)))

;; (map (fn [[a b]] (- b a)) (partition 2 1 (map #(rem % 10) (take 11 (iterate next-secret-number 123)))))

(defn index-of
  "Returns the first index where needle is found in haystack.
  Returns -1 if the needle is  not found in haystack."
  [needle haystack]
  (loop [n needle h haystack index 0]
    (let [n-length (count n)
          h-length (count h)]
      (cond
        (> n-length h-length) -1
        (= n (subvec h 0 n-length)) index
        :else (recur n (subvec h 1) (inc index))))))

(defn part2 [nums change-sequence]
  (let [prices (fn [secret-number] (mapv #(rem % 10) (take 2001 (iterate next-secret-number secret-number))))
        price-changes (fn [coll] (mapv (fn [[a b]] (- b a)) (partition 2 1 coll)))]
    (reduce (fn [acc secret-number]
              (let [p (prices secret-number)
                    pc (price-changes p)
                    idx (index-of change-sequence pc)]
                ;; (println idx)
                (if (>= idx 0)
                  (+ acc (nth p (+ 4 idx)))
                  acc))) 0 nums)))

(defn total-unique-combinations [nums]
  (reduce (fn [acc n]
            (let [p (mapv #(rem % 10) (take 2001 (iterate next-secret-number n)))
                  pc  (mapv (fn [[a b]] (- b a)) (partition 2 1 p))]
              (set/union acc (into #{} (distinct (partition 4 1 pc)))))) #{} nums))


(defn count-bananas [coll change-sequence]
  (reduce + (map (fn [[prices price-changes]]
                   (let [idx (index-of change-sequence price-changes)]
                     (if (>= idx 0)
                       (nth prices (+ 4 idx))
                       0))) coll)))

(defn cache-prices [nums]
  (mapv (fn [n] (let [p (mapv #(rem % 10) (take 2001 (iterate next-secret-number n)))
                      pc  (mapv (fn [[a b]] (- b a)) (partition 2 1 p))]
                  (vector p pc))) nums))

(defn -main []
  (let [input (->> (slurp "resources/input.txt")
                   s/split-lines
                   (map #(Long/parseLong %)))
        c (cache-prices input)
        combs (take 10000 (drop 30000 (total-unique-combinations input)))]
    (time (println
           (apply max (map #(count-bananas c %) combs))))))