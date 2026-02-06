(ns intcode-new.intcode
  (:require [clojure.string :as str]))

;; (def state {:mem [1 9 10 3 2 3 11 0 99 30 40 50] :pc 0 :in [] :out []})

(defn parse-input [filepath]
  (let [s (str/trim (slurp filepath))
        s' (str/split s #",")]
    (mapv Integer/parseInt s')))

(defn digits [n]
  (mapv #(Character/digit ^char % 10) (str n)))

(defn decode [instr]
  (let [op (mod instr 100)
        modes (mapv #(mod (quot instr %) 10) [100 1000 10000])]
    {:opcode op :modes modes}))

(defn load-program [program]
  {:mem program :pc 0 :in [1] :out []})

(defn mem-read [st addr]
  (get (:mem st) addr 0))

(defn mem-write [st addr val]
  (assoc-in st [:mem addr] val))

(defn param [st offset mode]
  (let [p (mem-read st (+ (:pc st) offset))]
    (if (zero? mode) (mem-read st p) p)))

(defn target-addr [st offset]
  (mem-read st (+ (:pc st) offset)))

(defmulti instruction (fn [st] (:opcode (decode (mem-read st (:pc st))))))

(defn- exec-arithmetic
  [{:keys [pc] :as st}]
  (let [op (mem-read st pc)
        {:keys [opcode modes]} (decode op)
        p1 (param st 1 (first modes))
        p2 (param st 2 (second modes))
        p3 (mem-read st (+ pc 3))
        val ((if (= opcode 1) + *) p1 p2)]
    (-> st
        (mem-write p3 val)
        (update :pc + 4))))

(defmethod instruction 1
  [state]
  (exec-arithmetic state))

(defmethod instruction 2
  [state]
  (exec-arithmetic state))

(defmethod instruction 3
  [{:keys [in pc] :as st}]
  (let [p1 (mem-read st (inc pc))
        val (first in)]
    (-> st
        (mem-write p1 val)
        (update :in rest)
        (update :pc + 2))))

(defmethod instruction 4
  [{:keys [pc] :as st}]
  (let [op (mem-read st pc)
        {:keys [opcode modes]} (decode op)
        val (param st 1 (first modes))]
    (-> st
        (update :out conj val)
        (update :pc + 2))))

(defn run [{:keys [mem pc] :as st}]
  (loop [state st]
    (let [op (mem-read state (:pc state))
          {:keys [opcode]} (decode op)]
      (if (= 99 opcode)
        state
        (recur (instruction state))))))
