(ns intcode-new.core
  (:require [clojure.string :as str]))

(def state {:program [1 9 10 3 2 3 11 0 99 30 40 50] :memory [] :pc 0 :input [] :output []})

(defn parse-input [filepath]
  (let [s (str/trim (slurp filepath))
        s' (str/split s #",")]
    (mapv Integer/parseInt s')))

(defn digits [n]
  (map #(read-string (str %)) (str n)))

(defn parse-opcode [opcode]
  (let [op (rem opcode 100)
        digs (digits opcode)
        [a b c] (concat (repeat (- 5 (count digs)) 0) digs)]
    {:mode-of-1st-parameter c
     :mode-of-2nd-parameter b
     :mode-of-3rd-parameter a
     :opcode op}))

(defn load-program [program]
  {:program program :pc 0 :input [1] :output []})

(defn- resolve-address [parameter mode memory]
  (if (zero? mode)
    (get memory parameter)
    parameter))

(defmulti instruction (fn [opcode state] opcode))

(defn- exec-arithmetic
  [_ {:keys [program memory pc] :as state}]
  (let [[op p1 p2 p3] (subvec program pc)
        {:keys [opcode mode-of-1st-parameter mode-of-2nd-parameter mode-of-3rd-parameter]} (parse-opcode op)
        res ((if (= opcode 1) + *) (resolve-address p1 mode-of-1st-parameter program) (resolve-address p2 mode-of-2nd-parameter program))]
    (-> state
        (assoc-in [:program p3] res)
        (update :pc + 4))))

(defmethod instruction 1
  [_ state]
  (exec-arithmetic 1 state))

(defmethod instruction 2
  [_ state]
  (exec-arithmetic 2 state))

(defmethod instruction 3
  [_ {:keys [program memory pc] :as state}]
  (let [[opcd p1] (subvec program pc)]
    (-> state
        (assoc-in [:program p1] (first (:input state)))
        (update :pc + 2))))

(defmethod instruction 4
  [_ {:keys [program memory pc] :as state}]
  (let [[op p1] (subvec program pc)
        {:keys [opcode mode-of-1st-parameter]} (parse-opcode op)
        value (resolve-address p1 mode-of-1st-parameter program)]
    (-> state
        (update :output conj value)
        (update :pc + 2))))

(defn run [{:keys [program pc] :as state}]
  (loop [current-state state]
    (let [op (get (:program current-state) (:pc current-state))
          {:keys [opcode]} (parse-opcode op)]
      (if (= 99 opcode)
        current-state
        (recur (instruction opcode current-state))))))

(defn get-input [{:keys [program] :as state} noun verb]
  (-> state
      (assoc-in [:program 1] noun)
      (assoc-in [:program 2] verb)))

(defn p1 [input noun verb]
  (first (:program  (run (get-input (load-program input) noun verb)))))

(defn p2 [input]
  (let [[n v] (first (filter #(= 19690720 (p1 input (first %) (second %))) (for [noun (range 100) verb (range 100)] [noun verb])))]
    (+ (* 100 n) v)))

(defn day5-p1 [input]
  (let [final-state (run (load-program input))]
    (last (:output final-state))))
