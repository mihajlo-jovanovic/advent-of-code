(ns day24.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn process-list [coll reg]
  (reduce (fn [m [[a op b] x]]
            (if (and (some? (get m a)) (some? (get m b)))
              (assoc m x  (case op
                            "AND" (bit-and (get m a) (get m b))
                            "XOR" (bit-xor (get m a) (get m b))
                            "OR" (bit-or  (get m a) (get m b))))
              m))
          reg coll))

(defn to-long [register v]
  (Long/parseLong (->> (filter (fn [[k _]] (= v (first k))) register)
                       sort
                       (map second)
                       reverse
                       (apply str)) 2))

(defn part1 [register p2]
  (let [p2-new (last (take 100 (iterate (partial process-list p2) register)))]
    (to-long p2-new \z)))

(defn valid? [register p2]
  (let [x (to-long register \x)
        y (to-long register \y)
        result (part1 register p2)]
    (= (+ x y) result)))

(defn swap-pair [p2 idx1 idx2]
  (let [tmp (get-in p2 [idx1 1])]
    (assoc-in (assoc-in p2 [idx1 1] (get-in p2 [idx2 1])) [idx2 1] tmp)))

;; used to find the correct pair to swap in REPL

;; (defn set-register-to-n [register c n]
;;   (let [ones (map first (filter #(= \1 (second %)) (map-indexed vector (char-array (reverse (Long/toBinaryString n))))))]
;;     (reduce (fn [m i] (let [k (str c (if (< i 10) (str "0" i) i))] (assoc m k 1))) register ones)))

;; (defn solve [register p2]
;;   (for [x (range 256)
;;         y (range 256)
;;         :when (not (valid? (set-register-to-n (set-register-to-n register \x (bit-shift-left x 40)) \y (bit-shift-left y 40)) p2))]
;;     [x y]))

;; (defn helper [p2]
;;   (let [i 111
;;         register-tmp-x (reduce (fn [acc i] (let [tmp (str "x" (if (< i 10) (str "0" i) i))] (assoc acc tmp 0))) {} (range 45))
;;         register-init (reduce (fn [acc i] (let [tmp (str "y" (if (< i 10) (str "0" i) i))] (assoc acc tmp 0))) register-tmp-x (range 45))
;;         register (set-register-to-n register-init \y (bit-shift-left 2 32))]
;;     (for [idx (range (count p2))
;;           :when (and (not= idx i) (valid? register (swap-pair p2 i idx)))]
;;       idx)))

(defn -main []
  (let [filename  "resources/input.txt"
        [p1 p2] (->  (slurp filename)
                     (s/split #"\n\n"))
        p1 (->> p1
                s/split-lines
                (map #(let [[a b] (s/split % #": ")]
                        [a (Integer/parseInt b)])))
        p2 (->> p2
                s/split-lines
                (map #(let [[a b] (s/split % #" -> ")]
                        [(s/split a #" ") b])))
        register (into {} p1)
        ans [62 92 19 205 195 59 117 111]]
    (time (println "Part 1: " (part1 register p2)))
    (time (println (valid? register (swap-pair (swap-pair (swap-pair (swap-pair (into [] p2) 62 92) 19 205) 195 59) 117 111))))
    (println "Part 2: " (s/join "," (sort (mapv #(second (get (into [] p2) %)) ans))))))