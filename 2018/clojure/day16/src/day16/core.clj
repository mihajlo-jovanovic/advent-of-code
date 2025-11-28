(ns day16.core
  (:require [clojure.string :as str]))

(defn parse-sample [text]
  (let [[before-line instr-line after-line] (str/split-lines text)
        parse-nums (fn [s] (mapv read-string (re-seq #"\d+" s)))]
    {:before (parse-nums before-line)
     :instruction (parse-nums instr-line)
     :after (parse-nums after-line)}))

(defn parse-input [filename]
  (let [input (slurp filename)
        [first-part second-part] (str/split input #"\n\n\n")
        samples (str/split first-part #"\n\n")]
    (map parse-sample samples)))

(defn parse-input-p2 [filename]
  (let [input (slurp filename)
        [first-part second-part] (str/split input #"\n\n\n\n")
        program (str/split-lines second-part)]
    (map #(mapv read-string (re-seq #"\d+" %)) program)))

;;(def before [3, 2, 1, 1])
;;
;;(def after [3, 2, 2, 1])
;;
;;(def inst [9, 2, 1, 2])

(defn validate [f {:keys [instruction before after]}]
  (= after (f instruction before)))

;; opcode implementations
(defn addr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (+ first-input second-input))))

(defn addi [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (+ first-input second-input))))

(defn mulr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (* first-input second-input))))

(defn muli [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (* first-input second-input))))

(defn banr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (bit-and first-input second-input))))

(defn bani [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (bit-and first-input second-input))))

(defn borr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (bit-or first-input second-input))))

(defn bori [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (bit-or first-input second-input))))

(defn setr [[opcode a b c] registers]
  (let [first-input (get registers a)]
    (assoc registers c first-input)))

(defn seti [[opcode a b c] registers]
  (let [first-input a]
    (assoc registers c first-input)))

(defn gtir [[opcode a b c] registers]
  (let [first-input a
        second-input (get registers b)]
    (assoc registers c (if (> first-input second-input) 1 0))))

(defn gtri [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (if (> first-input second-input) 1 0))))

(defn gtrr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (if (> first-input second-input) 1 0))))

(defn eqir [[opcode a b c] registers]
  (let [first-input a
        second-input (get registers b)]
    (assoc registers c (if (= first-input second-input) 1 0))))

(defn eqri [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input b]
    (assoc registers c (if (= first-input second-input) 1 0))))

(defn eqrr [[opcode a b c] registers]
  (let [first-input (get registers a)
        second-input (get registers b)]
    (assoc registers c (if (= first-input second-input) 1 0))))

(def all-opcodes [addr, addi, mulr, muli, banr, bani, borr, bori, setr, seti, gtir, gtri, gtrr, eqri, eqir, eqrr])

(defn behaves-like-three-or-more? [sample]
  (let [cnt (reduce + (map #(if (validate % sample) 1 0) all-opcodes))]
    (> cnt 2)))

(defn p1 []
  (let [input-file "resources/day16.txt"
        samples (parse-input input-file)]
    (count (filter behaves-like-three-or-more? samples))))

;; 12 -> mulr
;;  4 -> addr
;;  1 -> borr
;; 13 -> eqri
;;  0 -> bori
;;  2 -> addi
;;  3 -> muli
;;  6 -> gtri
;;  8 -> gtrr
;;  9 -> seti
;; 10 -> eqir
;; 11 -> eqrr
;; 14 -> gtir
;; 15 -> banr
;;  5 -> bani
;;  7 -> setr

(def opcode-key [bori, borr, addi, muli, addr, bani, gtri, setr, gtrr, seti, eqir, eqrr, mulr, eqri, gtir, banr])

(defn p2 []
  (let [input-file "resources/day16.txt"
        program (parse-input-p2 input-file)
        register [0 0 0 0]]
    (reduce (fn [r [op a b c]] (let [f (get opcode-key op)] (f [op a b c] r))) register program)))
