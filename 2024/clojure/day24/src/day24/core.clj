(ns day24.core
  (:gen-class)
  (:require [clojure.string :as s]))

;; (def p1 (map #(let [[a b] (clojure.string/split % #": ")] (vector a (Integer/parseInt b))) (clojure.string/split-lines (first parts))))

;; (def p2 (map #(let [[a b] (clojure.string/split % #" -> ")] (vector (clojure.string/split a #" ") b)) (clojure.string/split-lines (second parts)))))

;; (def register {})

;; (defn resolve-x [x]
;;   (if (or (= 1 x) (= 0 x))
;;     x
;;     (get register x)))

;; (defn ev [[x op y]]
;;   (case op
;;     "AND" (and (resolve-x x) (resolve-x y))
;;     "XOR" (bit-xor (resolve-x x) (resolve-x y))
;;     "OR" (or (resolve-x x) (resolve-x y))))

(defn process-list [coll reg]
  (reduce (fn [m [[a op b] x]]
            (if (and (some? (get m a)) (some? (get m b)))
              (assoc m x  (case op
                            "AND" (bit-and (get m a) (get m b))
                            "XOR" (bit-xor (get m a) (get m b))
                            "OR" (bit-or  (get m a) (get m b))))
              m))
          reg coll))

;; (Long/parseLong (apply str (reverse (map second (sort (filter (fn [[k _]] (= \z (first k))) (last (take 4 (iterate (partial process-list p2) register)))))))) 2)

(defn to-long [register v]
  (Long/parseLong (->> (filter (fn [[k _]] (= v (first k))) register)
                       sort
                       (map second)
                       reverse
                       (apply str)) 2))

(defn part1 [register p2]
  (let [p2-new (last (take 100 (iterate (partial process-list p2) register)))]
    (to-long p2-new \z)))

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
        register (into {} p1)]
    (println (count p2))
    (time (println "Part 1: " (part1 register p2)))))