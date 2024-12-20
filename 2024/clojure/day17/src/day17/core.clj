(ns day17.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.math :refer [pow]]))

(defn parse-input [s]
  (map #(Integer/parseInt %) (s/split s #",")))

(defn parse-instruction [[opcode operand]]
  {:opcode opcode :operand operand})

(defn resolve-value [registers v]
  (if (get registers v) (get registers v) (Long/parseLong v)))

;; define multi-methods for each instruction type
(defmulti process-instruction (fn [_ instruction] (:opcode instruction)))

;;bxl
(defmethod process-instruction 1 [{:keys [registers] :as state} {:keys [operand]}]
  (let [v (resolve-value registers \B)]
    (-> state
        (assoc :registers (assoc registers \B (bit-xor operand v)))
        (update :pc inc))))

(defn resolve-combo-operand
  "Combo operands 0 through 3 represent literal values 0 through 3.
Combo operand 4 represents the value of register A.
Combo operand 5 represents the value of register B.
Combo operand 6 represents the value of register C."
  [registers operand]
  (case operand
    0 0
    1 1
    2 2
    3 3
    4 (get registers \A)
    5 (get registers \B)
    6 (get registers \C)))

;;adv
(defmethod process-instruction 0 [{:keys [registers] :as state} {:keys [operand]}]
  (let [numerator (resolve-value registers \A)
        denominator (pow 2 (resolve-combo-operand registers operand))]
    (-> state
        (assoc :registers (assoc registers \A (long (/ numerator denominator))))
        (update :pc inc))))

;;bst
(defmethod process-instruction 2 [{:keys [registers] :as state} {:keys [operand]}]
  (-> state
      (assoc :registers (assoc registers \B (mod (resolve-combo-operand registers operand) 8)))
      (update :pc inc)))


;; jnz
(defmethod process-instruction 3 [{:keys [registers] :as state} {:keys [operand]}]
  (let [v (resolve-value registers \A)]
    (if (not= 0 v)
      (assoc state :pc operand)
      (update state :pc inc))))

;;bxc
(defmethod process-instruction 4 [{:keys [registers] :as state} _]
  (let [v1 (resolve-value registers \B)
        v2 (resolve-value registers \C)]
    (-> state
        (assoc :registers (assoc registers \B (bit-xor v1 v2)))
        (update :pc inc))))

;;out
(defmethod process-instruction 5 [{:keys [registers] :as state} {:keys [operand]}]
  (let [v (resolve-combo-operand registers operand)]
    (-> state
        (update :out conj (mod v 8))
        (update :pc inc))))

;;bdv
(defmethod process-instruction 6 [{:keys [registers] :as state} {:keys [operand]}]
  (let [numerator (resolve-value registers \A)
        denominator (pow 2 (resolve-combo-operand registers operand))]
    (-> state
        (assoc :registers (assoc registers \B (int (/ numerator denominator))))
        (update :pc inc))))


;;cdv
(defmethod process-instruction 7 [{:keys [registers] :as state} {:keys [operand]}]
  (let [numerator (resolve-value registers \A)
        denominator (pow 2 (resolve-combo-operand registers operand))]
    (-> state
        (assoc :registers (assoc registers \C (long (/ numerator denominator))))
        (update :pc inc))))

(defn run-program [state instructions]
  (loop [state state]
    (let [pc (:pc state)]
      (if (>= pc (count instructions))
        state
        (recur (process-instruction state (instructions pc)))))))

(defn initalize-registers []
  (zipmap '(\A \B \C) (repeat 0)))

(defn load-program [registers]
  {:registers registers
   :pc 0
   :out []})

;; Part 2
(defn part2-recursive [n acc program instructions]
  (if (empty? n)
    acc
    (let [output-to-match (drop (- (count program) (inc (- (count program) (count n)))) program)
          reverse-engineer2 (fn [new-a]
                              (map #(vector (bit-or new-a %) (:out (run-program (load-program (assoc (initalize-registers) \A (bit-or new-a %))) instructions))) (range 0 8)))
          nxt (reverse-engineer2 (bit-shift-left acc 3))]
      ;; backtracking here is key; as there are multiple possible matches at some levels and not all result in a valid solution
      (some (fn [[new-a out]] (and (= out output-to-match) (part2-recursive (rest n) new-a program instructions))) nxt))))

(defn -main
  []
  (let [program-str "2,4,1,2,7,5,4,5,1,3,5,5,0,3,3,0"
        program (parse-input program-str)
        instructions (into [] (map parse-instruction (partition 2 program)))]
    (println "Part 2: " (part2-recursive (reverse program) 0 program instructions))))