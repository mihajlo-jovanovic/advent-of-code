;"borrowed" from https://github.com/djblue/advent-of-code/blob/master/src/advent_of_code/core_2019.clj
(ns intcode.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.core.async :as a :refer [>! <! <!! >!!]]))

(defn get-input [file]
  (->> file io/resource slurp))

(defn file->vec [file]
  (read-string (str "[" (get-input file) "]")))

(defn digits [x]
  (if (< x 10)
    [x]
    (conj (digits (quot x 10)) (rem x 10))))

(defn parse-opcode [op]
  (let [[a b c d e]
        (->> op
             digits
             (concat (repeat 5 0))
             (take-last 5))]
    [(+ (* 10 d) e) c b a]))

(defn resolve-addr [addr mode state]
  (case mode
    2 (+ (get state :relative-base 0) addr) addr))

(defn vec->map [v]
  (into {} (map-indexed #(-> [%1 %2]) v)))

(defn memory-read [addr mode state]
  (case mode
    1 addr
    (get-in state [:memory (resolve-addr addr mode state)] 0)))

(def op->fn {1 + ' 2 *'
             5 not= 6 =
             7 #(if (< %1 %2) 1 0)
             8 #(if (= %1 %2) 1 0)})

(defn run-new-async [state in out]
  (a/go-loop [state (update state :memory vec->map)]
    (let [{:keys [pc memory relative-base]
           :or { pc 0 relative-base 0}}
          state
          [op & args] [(get memory pc)
                       (get memory (+ 1 pc) 0)
                       (get memory (+ 2 pc) 0)
                       (get memory (+ 3 pc) 0)]
          [op & modes] (parse-opcode op)
          f (op->fn op)
          pc (inc pc)]
      (if (= op 99) ;we're done!
        (do (a/close! out) state)
        (recur
         (merge
          state
          (case op
          ; arithmetic / less than / equals
            (1 2 7 8)
            (let [[a b c] args
                  [m1 m2 m3] modes
                  a (memory-read a m1 state)
                  b (memory-read b m2 state)
                  c (resolve-addr c m3 state)
                  memory (assoc memory c (f a b))]
              {:memory memory :pc (+ pc 3)})
            3
            (let [val (<! in)
                  [addr] args
                  addr (resolve-addr addr (first modes) state)
                  memory (assoc memory addr val)]
              {:memory memory :pc (inc pc)})
            4
            (let [[addr] args
                  [mode] modes
                  val (memory-read addr mode state)]
              (>! out val)
              {:memory memory :pc (inc pc)})
            ; jump-if-true / jump-if-false
            (5 6)
            (let [[value dest] args
                  [m1 m2] modes
                  value (memory-read value m1 state)
                  dest (memory-read dest m2 state)]
              {:pc (if (f value 0) dest (+ pc 2))})
           ; adjusts the relative base
            9
            (let [[addr] args
                  [mode] modes
                  value (memory-read addr mode state)]
              {:relative-base (+ relative-base value) :pc (inc pc)}))))))) )

(defn intcode-vm-new [state]
  (let [in (a/to-chan (:in state)) out (a/chan (a/sliding-buffer 100))
        [state] (a/alts!! [(run-new-async state in out)
                           (a/timeout 5000)])]
    (a/close! out)
    (when state
      (assoc state :out (or (<!! (a/into '() out)) [])))))

(defn run-program-new [program in]
  (-> {:memory program :in in} intcode-vm-new :out first))
