(ns day11.core
  (:gen-class))

(defn divisible?
  "Determine if a number is divisible by the divisor with no remainders."
  [div num]
  (zero? (mod num div)))

(defn turn [coll i]
  "Perform single monkey's turn - throwing all of its items"
  (let [m (coll i)
        prd (* 2 3 5 7 11 13 17 19)]
    (loop [items (:items m)
           agg coll]
      (if (empty? items)
        agg
        (let [item (mod ((:op m) (first items)) prd)
              throw-to ((:pred m) item)
              tmp (update-in agg [throw-to :items] #(vec (conj % item)))]
          (recur (rest items) (update-in tmp [i :items] rest)))))))

(defn round [{:keys [monkeys inspect-count]}]
  (loop [m monkeys
         i 0
         counters inspect-count]
    (if (= i (count m))
      {:monkeys m :inspect-count counters}
      (let [num-of-items (count (:items (m i)))]
        (recur (turn m i) (inc i) (update counters i (partial + num-of-items)))))))

(defn monkey [coll op pred]
  {:items coll :op op :pred pred})
;(def m1 (monkey [79, 98] #(* % 19) #(if (divisible? 23 %) 2 3)))
;(def m2 (monkey [54, 65, 75, 74] #(+ % 6) #(if (divisible? 19 %) 2 0)))
;(def m3 (monkey [79, 60, 97] #(* % %) #(if (divisible? 13 %) 1 3)))
;(def m4 (monkey [74] #(+ % 3) #(if (divisible? 17 %) 0 1)))
;(def monkeys [m1 m2 m3 m4])
(def m1 (monkey [50, 70, 89, 75, 66, 66] #(* % 5) #(if (divisible? 2 %) 2 1)))
(def m2 (monkey [85] #(* % %) #(if (divisible? 7 %) 3 6)))
(def m3 (monkey [66, 51, 71, 76, 58, 55, 58, 60] #(inc %) #(if (divisible? 13 %) 1 3)))
(def m4 (monkey [79, 52, 55, 51] #(+ % 6) #(if (divisible? 3 %) 6 4)))
(def m5 (monkey [69, 92] #(* % 17) #(if (divisible? 19 %) 7 5)))
(def m6 (monkey [71, 76, 73, 98, 67, 79, 99] #(+ % 8) #(if (divisible? 5 %) 0 2)))
(def m7 (monkey [82, 76, 69, 69, 57] #(+ % 7) #(if (divisible? 11 %) 7 4)))
(def m8 (monkey [65, 79, 86] #(+ % 5) #(if (divisible? 17 %) 5 0)))
(def monkeys [m1 m2 m3 m4 m5 m6 m7 m8])
(defn monkey [coll op pred]
  {:items coll :op op :pred pred})

(defn -main
  [& _]
  (->> (take 10001 (iterate round {:monkeys monkeys :inspect-count [0 0 0 0 0 0 0 0]}))
       (last)
       (:inspect-count)
       (sort)
       (drop (- (count monkeys) 2))
       (reduce *)
       (println)))