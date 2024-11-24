(ns day23.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn enqueue [queue value]
  (conj queue value))

(defn dequeue [queue]
  (if (empty? queue)
    {:element nil :queue queue}
    {:element (peek queue) :queue (pop queue)}))

(defn parse-instruction [s]
  (let [[op op1 op2] (s/split s #" ")]
    {:Type (keyword (read-string op)) :Op1 op1 :Op2 op2}))

(defn resolve-value [registers v]
  (if (get registers v) (get registers v) (Long/parseLong v)))

;; define multi-methods for each instruction type
(defmulti process-instruction (fn [_ instruction] (:Type instruction)))

(defmethod process-instruction :set [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (assoc registers Op1 v))
        (update :pc inc))))

(defmethod process-instruction :add [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 + v))
        (update :pc inc))))

(defmethod process-instruction :sub [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 - v))
        (update :pc inc))))

(defmethod process-instruction :mul [{:keys [registers mul-count] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 * v))
        (assoc :mul-count (inc mul-count))
        (update :pc inc))))

(defmethod process-instruction :mod [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 mod v))
        (update :pc inc))))

(defmethod process-instruction :snd [{:keys [registers send-queue sent-count] :as state} {:keys [Op1]}]
  (let [v (resolve-value registers Op1)
        new-queue (enqueue send-queue v)]
    (-> state
        (assoc :sent-count (inc sent-count))
        (assoc :send-queue new-queue)
        (update :pc inc))))

(defmethod process-instruction :rcv [{:keys [registers recv-queue] :as state} {:keys [Op1]}]
  (if (= 0 (count recv-queue))
    (-> state (assoc :suspended true))
    (let [{:keys [element queue]} (dequeue recv-queue)]
      (-> state
          (assoc :registers (assoc registers Op1 element))
          (assoc :recv-queue queue)
          (update :pc inc)))))

(defmethod process-instruction :jgz [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (if (> (resolve-value registers Op1) 0)
    (-> state
        (update :pc (fn [x] (+ x (resolve-value registers Op2)))))
    (update state :pc inc)))

(defmethod process-instruction :jnz [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (if (not= (resolve-value registers Op1) 0)
    (-> state
        (update :pc (fn [x] (+ x (resolve-value registers Op2)))))
    (update state :pc inc)))

(defn run-program [state instructions]
  (loop [state state]
    (let [pc (:pc state)
          suspended? (:suspended state)]
      (if (or suspended? (>= pc (count instructions)))
        state
        (recur (process-instruction state (instructions pc)))))))

(defn initalize-registers []
  (zipmap (map #(str (char %)) (range 97 123)) (repeat 0)))

(defn load-program [registers]
  {:registers registers
   :send-queue clojure.lang.PersistentQueue/EMPTY
   :recv-queue clojure.lang.PersistentQueue/EMPTY
   :pc 0
   :sent-count 0
   :mul-count 0
   :suspended false})

(defn solve-p1 [filename]
  (let [instructions (vec (map parse-instruction (s/split-lines (slurp filename))))
        state (load-program (initalize-registers))]
    (loop [state state]
      (let [state (run-program state instructions)]
        (if  (or (:suspended state) (>= (:pc state) (count instructions)))
          (:mul-count state)
          (recur state))))))

(defn sieve
  [[p & xs]]
  (cons p
        (lazy-seq
         (sieve (for [x xs :when (pos? (mod x p))]
                  x)))))

(def primes (sieve (iterate inc 2)))

(defn prime?
  [x]
  (let [factors-to-try (fn [x] (take-while #(<= (* % %) x) primes))]
    (every? #(pos? (mod x %)) (factors-to-try x))))

(defn -main []
  (println "Part 1: " (solve-p1 "resources/input.txt"))
  (println "Part 2: " (- 1001 (count (filter prime? (range 109300 126300 17))))))  ;; code's just counting composite numbers in given range