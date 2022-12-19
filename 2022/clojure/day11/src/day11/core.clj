(ns day11.core
  (:gen-class))

(defn divisible?
  "Determine if a number is divisible by the divisor with no remainders."
  [div num]
  (zero? (mod num div)))

;(defn monkey-throw [m i]
;  (let [op1 #(* % 19)
;        to #(if (divisible? %1 %4) %2 %3)
;        to1 (partial to 23 2 3)
;        op2 #(+ % 6)
;        to2 (partial to 19 2 0)
;        op3 #(* % %)
;        to3 (partial to 13 1 3)
;        op4 #(+ % 3)
;        to4 (partial to 17 0 1)]
;    (case m
;      0 [(to1 (quot (op1 i) 3)) (quot (op1 i) 3)]
;      1 [(to2 (quot (op2 i) 3)) (quot (op2 i) 3)]
;      2 [(to3 (quot (op3 i) 3)) (quot (op3 i) 3)]
;      3 [(to4 (quot (op4 i) 3)) (quot (op4 i) 3)])))

(defn monkey-throw [m i]
  (let [op1 #(* % 19)
        to #(if (divisible? %1 %4) %2 %3)
        to1 (partial to 23 2 3)
        op2 #(+ % 6)
        to2 (partial to 19 2 0)
        op3 #(* % %)
        to3 (partial to 13 1 3)
        op4 #(+ % 3)
        to4 (partial to 17 0 1)]
    (case m
      0 [(to1 (op1 i)) (op1 i)]
      1 [(to2 (op2 i)) (op2 i)]
      2 [(to3 (op3 i)) (op3 i)]
      3 [(to4 (op4 i)) (op4 i)])))

;(defn monkey-throw [m i]
;  (let [op1 #(* % 5)
;        to #(if (divisible? %1 %4) %2 %3)
;        to1 (partial to 2 2 1)
;        op2 #(* % %)
;        to2 (partial to 7 3 6)
;        op3 #(+ % 1)
;        to3 (partial to 13 1 3)
;        op4 #(+ % 6)
;        to4 (partial to 3 6 4)
;        op5 #(* % 17)
;        to5 (partial to 19 7 5)
;        op6 #(+ % 8)
;        to6 (partial to 5 0 2)
;        op7 #(+ % 7)
;        to7 (partial to 11 7 4)
;        op8 #(+ % 5)
;        to8 (partial to 17 5 0)]
;    (case m
;      0 [(to1 (quot (op1 i) 3)) (quot (op1 i) 3)]
;      1 [(to2 (quot (op2 i) 3)) (quot (op2 i) 3)]
;      2 [(to3 (quot (op3 i) 3)) (quot (op3 i) 3)]
;      3 [(to4 (quot (op4 i) 3)) (quot (op4 i) 3)]
;      4 [(to5 (quot (op5 i) 3)) (quot (op5 i) 3)]
;      5 [(to6 (quot (op6 i) 3)) (quot (op6 i) 3)]
;      6 [(to7 (quot (op7 i) 3)) (quot (op7 i) 3)]
;      7 [(to8 (quot (op8 i) 3)) (quot (op8 i) 3)])))

(defn round-helper [coll m]
  (if (empty? (nth coll m))
    coll
    (let [items (nth coll m)
          [m2 w] (monkey-throw m (first items))]
      (round-helper (assoc coll m (vec (rest (nth coll m))) m2 (conj (nth coll m2) w)) m))))

(defn round [coll]
  (loop [state (first coll)
         m 0
         result (second coll)]
    (if (= m (count state))
      (vector state result)
      (let [inspected (count (nth state m))
            new-state (round-helper state m)]
        (recur new-state (inc m) (assoc result m (+ inspected (get result m))))))))

(def s1 [79, 98])
(def s2 [54, 65, 75, 74])
(def s3 [79, 60, 97])
(def s4 [74])

(def m1 [50, 70, 89, 75, 66, 66])
(def m2 [85])
(def m3 [66, 51, 71, 76, 58, 55, 58, 60])
(def m4 [79, 52, 55, 51])
(def m5 [69, 92])
(def m6 [71, 76, 73, 98, 67, 79, 99])
(def m7 [82, 76, 69, 69, 57])
(def m8 [65, 79, 86])

(defn -main
  [& input]
  (let [input [s1 s2 s3 s4]
        monkeys (count input)
        inspect-count (into (vector) (repeat monkeys 0))]
    (->> (take 21 (iterate round (vector input inspect-count)))
         (last)
         (second)
         (sort)
         (drop (- monkeys 2))
         (reduce *)
         (println "Part 1 solution: "))))