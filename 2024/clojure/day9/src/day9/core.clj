(ns day9.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (let [input (str (s/trim-newline (slurp filename)) "0")]
    (map (fn [[x y]]
           [(Integer/parseInt (str x))
            (Integer/parseInt (str y))])
         (partition 2 input))))

(defn defrag
  "Helper function to compact the diskmap"
  [diskmap acc]
  (let [[i [n1 n2]] (first diskmap)
        [j [m1 _]]  (last diskmap)
        size (count diskmap)]
    (cond
      (<= size 1)
      (into acc (repeat n1 i))

      (pos-int? n1)
      (recur (assoc diskmap 0 [i [0 n2]])
             (into acc (repeat n1 i)))

      (> n2 m1)
      (recur (assoc (pop diskmap) 0 [i [n1 (- n2 m1)]])
             (into acc (repeat m1 j)))

      :else
      (let [diskmap' (vec (rest diskmap))
            last-idx (dec (count diskmap'))]
        (recur (update-in diskmap' [last-idx 1 0] - n2)
               (into acc (repeat n2 j)))))))

(defn score [diskmap]
  (reduce + (map-indexed (fn [i n] (* i n)) diskmap)))

(defn part1 [input]
  (let [diskmap (into [] (map-indexed vector (parse-input input)))
        after-compaction (defrag diskmap [])]
    (score after-compaction)))

(defn list-insert [lst elem index]
  (let [[l r] (split-at index lst)]
    (concat l [elem] r)))

(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn move-whole-file [diskmap file-id idx-to-insert]
  (let [[idx [fid [n1 n2]]] (some (fn [[i [k [x y]]]] (when (= k file-id) [i [k [x y]]])) (map-indexed vector diskmap))
        [_ [_ m2]] (nth diskmap idx-to-insert)
        diskmap' (assoc-in diskmap [idx-to-insert 1 1] 0)
        diskmap'' (vec-remove idx diskmap')
        diskmap''' (list-insert diskmap'' [fid [n1 (- m2 n1)]] (inc idx-to-insert))]
    (if (= idx (inc idx-to-insert))
      (into [] (list-insert diskmap'' [fid [n1 (+ n2 n1 (- m2 n1))]] (inc idx-to-insert)))
      (update-in (into [] diskmap''') [idx 1 1] + (+ n1 n2)))))

(defn helper-p2 [diskmap]
  (loop [diskmap diskmap
         file-id (dec (count diskmap))]
    (if (zero? file-id)
      diskmap
      (let [[_ [n1 _]] (first (filter (fn [[k [_ _]]] (= k file-id)) diskmap))
            idx-to-insert (ffirst (filter (fn [[_ [_ [_ m2]]]] (>= m2 n1)) (map-indexed vector (take-while (fn [[i [_ _]]] (not= i file-id)) diskmap))))]
        (if idx-to-insert
          (recur (move-whole-file diskmap file-id idx-to-insert) (dec file-id))
          (recur diskmap (dec file-id)))))))

(defn part2 [input]
  (let [diskmap (into [] (map-indexed vector (parse-input input)))
        after-compaction (helper-p2 diskmap)
        expanded-form (reduce (fn [a [i [n1 n2]]] (concat a (vec (repeat n1 i)) (vec (repeat n2 0)))) [] after-compaction)]
    (score expanded-form)))

(defn -main []
  (println "Part 1: " (part1 "resources/input.txt"))
  (time (println "Part 2: " (part2 "resources/input.txt"))))