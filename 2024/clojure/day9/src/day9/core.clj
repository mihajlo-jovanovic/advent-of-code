(ns day9.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-input [filename]
  (let [input (concat (s/trim-newline (slurp filename)) "0")]
    (map (fn [[n1 n2]] [(Integer/parseInt (str n1)) (Integer/parseInt (str n2))]) (partition 2 input))))

(defn helper [diskmap acc]
  (let [[i [n1 n2]] (first diskmap)
        [j [m1 _]] (last diskmap)]
    (if (or (empty? diskmap) (= 1 (count diskmap)))
      (into [] (concat acc (vec (repeat n1 i))))
      (if (pos-int? n1)
        (recur (assoc diskmap 0 [i [0 n2]]) (into [] (concat acc (vec (repeat n1 i)))))
        (if (> n2 m1)
          (recur (assoc (pop diskmap) 0 [i [n1 (- n2 m1)]]) (into [] (concat acc (vec (repeat m1 j)))))
          (let [diskmap' (into [] (rest diskmap))]
            (recur (update-in diskmap' [(dec (count diskmap')) 1 0] - n2) (into [] (concat acc (vec (repeat n2 j)))))))))))

(defn part1 [input]
  (let [diskmap (into [] (map-indexed vector (parse-input input)))
        after-compaction (helper diskmap [])]
    (reduce + (map (fn [[i n]] (* i n)) (map-indexed vector after-compaction)))))

(defn list-insert [lst elem index]
  (let [[l r] (split-at index lst)]
    (concat l [elem] r)))

(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn move-whole-file [diskmap file-id idx-to-insert]
  (let [file-pos (first (filter (fn [[_ [k [_ _]]]] (= k file-id)) (map-indexed vector diskmap)))
        [idx [fid [n1 n2]]] file-pos
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
            idx-to-insert (first (first (filter (fn [[_ [_ [_ m2]]]] (>= m2 n1)) (map-indexed vector (take-while (fn [[i [_ _]]] (not= i file-id)) diskmap)))))]
        (if idx-to-insert
          (recur (move-whole-file diskmap file-id idx-to-insert) (dec file-id))
          (recur diskmap (dec file-id)))))))

(defn part2 [input]
  (let [diskmap (into [] (map-indexed vector (parse-input input)))
        after-compaction (helper-p2 diskmap)
        after-compaction' (into [] (reduce (fn [a [i [n1 n2]]] (concat a (vec (repeat n1 i)) (vec (repeat n2 0)))) [] after-compaction))]
    (reduce + (map (fn [[i n]] (* i n)) (map-indexed vector after-compaction')))))

(defn -main
  []
  (println "Part 1: " (part1 "resources/input.txt"))
  (println "Part 2: " (part2 "resources/input.txt")))