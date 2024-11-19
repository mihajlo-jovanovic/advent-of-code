(ns day18.core
  (:gen-class)
  (:require
   [clojure.string :as s]))

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

(defmethod process-instruction :mul [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 * v))
        (update :pc inc))))

(defmethod process-instruction :mod [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (let [v (resolve-value registers Op2)]
    (-> state
        (assoc :registers (update registers Op1 mod v))
        (update :pc inc))))

(defmethod process-instruction :snd [{:keys [registers send-queue sent-count] :as state} {:keys [Op1]}]
  (let [v (resolve-value registers Op1)
        new-queue (enqueue send-queue v)]
    ;; (println "Sending value: " v)
    (-> state
        (assoc :sent-count (inc sent-count))
        (assoc :send-queue new-queue)
        (update :pc inc))))

(defmethod process-instruction :rcv [{:keys [registers recv-queue] :as state} {:keys [Op1]}]
  (if (= 0 (count recv-queue))
    (-> state (assoc :suspended true))
    (let [{:keys [element queue]} (dequeue recv-queue)]
      ;; (println "Receiving value: " element)
      (-> state
          (assoc :registers (assoc registers Op1 element))
          (assoc :recv-queue queue)
          (update :pc inc)))))

(defmethod process-instruction :jgz [{:keys [registers] :as state} {:keys [Op1 Op2]}]
  (if (> (resolve-value registers Op1) 0)
    (-> state
        (update :pc (fn [x] (+ x (resolve-value registers Op2)))))
    (update state :pc inc)))

(defn run-program [state instructions]
  (loop [state state]
    (let [pc (:pc state)
          suspended? (:suspended state)]
      (if suspended?
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
   :suspended false})

(defn solve-p2 [filename]
  (let [instructions (vec (map parse-instruction (s/split-lines (slurp filename))))
        register-0 (initalize-registers)
        register-1 (assoc (initalize-registers) "p" 1)
        program-0 (load-program register-0)
        program-1 (load-program register-1)]
    (loop [program-0 program-0
           program-1 program-1]
      (let [program-0 (run-program program-0 instructions)
            program-1 (run-program (-> program-1
                                       (assoc :recv-queue (:send-queue program-0))
                                       (assoc :send-queue (:recv-queue program-0))
                                       (assoc :suspended false)) instructions)]
        (if (and (empty? (:send-queue program-1)) (empty? (:recv-queue program-1)))
          (:sent-count program-1)
          (let [program-0 (-> program-0
                              (assoc :send-queue (:recv-queue program-1))
                              (assoc :recv-queue (:send-queue program-1)) (assoc :suspended false))]
            (recur program-0  program-1)))))))

(defn -main []
  (println "Part 2: " (solve-p2 "resources/input.txt")))