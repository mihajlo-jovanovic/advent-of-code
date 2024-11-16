(ns day16.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-moves
  "Parses a string of moves into a list of maps."
  [input]
  (map (fn [move]
         (cond
           ;; :spin move (starts with 's')
           (= \s (first move))
           {:type :spin :value (Integer/parseInt (subs move 1))}

           ;; :exchange move (starts with 'x')
           (= \x (first move))
           (let [[a b] (map #(Integer/parseInt %) (s/split (subs move 1) #"/"))]
             {:type :exchange :a a :b b})

           ;; :partner move (all others)
           :else
           (let [[a b] (s/split (subs move 1) #"/")]
             {:type :partner :a a :b b})))
       (s/split input #",")))

;; Define a mutable state (atom)
(def state (atom {:programs "abcdefghijklmnop"}))

;; Define a multimethod based on operation type
(defmulti handle-operation (fn [op] (:type op)))

(defn do-spin [program x]
  (let [n (count program)]
    (str (subs program (- n x)) (subs program 0 (- n x)))))

;; Method for :spin operation
(defmethod handle-operation :spin
  [op]
  (swap! state update :programs do-spin (:value op)))

(defn do-exchange
  "Swaps two characters in a string at the given indices."
  [s idx1 idx2]
  (let [min-idx (min idx1 idx2) ; Ensure first index is the smaller
        max-idx (max idx1 idx2)]
    (if (= min-idx max-idx)
      s ; If indices are the same, return the string as-is
      (let [char1 (nth s min-idx) ; Get the character at idx1
            char2 (nth s max-idx) ; Get the character at idx2
          ; Rebuild the string with swapped characters
            swapped (str (subs s 0 min-idx) char2 (subs s (inc min-idx) max-idx) char1 (subs s (inc max-idx)))]
        swapped))))

;; Method for :exchange operation
(defmethod handle-operation :exchange
  [op]
  (swap! state update :programs do-exchange (:a op) (:b op)))

(defn do-partner [program a b]
  (let [idx-a (.indexOf program a)
        idx-b (.indexOf program b)]
    (do-exchange program idx-a idx-b)))

;; Method for :partner operation
(defmethod handle-operation :partner
  [op]
  (swap! state update :programs do-partner (:a op) (:b op)))

;; Default method for unknown operations
(defmethod handle-operation :default
  [op]
  (println "Unknown operation:" (:type op)))

;; Function to process a list of moves
(defn process-moves [moves]
  (doseq [move moves]
    ;; (println "Processing move: " move)
    (handle-operation move)))

;; Part 2: This section of commented out code is what I used to find the repeated state.
;; Note I had to change the implementation to not use global state, so that i can reuse
;; the old find-duplicate function for finding first repeated state in a lazy sequence.
;;
;; (def state2 {:programs "abcdefghijklmnop" :idx 0})
;;
;; (defn process-moves-p2 [moves state]
;;   (let [move (nth moves (:idx state))]
;;     (case (:type move)
;;       :spin {:programs (do-spin (:programs state) (:value move))
;;              :idx (mod (inc (:idx state)) 10000)}
;;       :exchange {:programs (do-exchange (:programs state) (:a move) (:b move))
;;                  :idx (mod (inc (:idx state)) 10000)}
;;       :partner {:programs (do-partner (:programs state) (:a move) (:b move))
;;                 :idx (mod (inc (:idx state)) 10000)})))
;;
;; (defn first-duplicate [coll]
;;   (reduce (fn [seen x]
;;             (if (contains? seen x)
;;               (reduced x)
;;               (conj seen x)))
;;           #{} coll))
;;
;; (def repeated-state {:programs "abcdefmhijklgnop", :idx 1})
;;
;; (defn get-index-of-repeated-state [moves state]
;;   (loop [counter 0
;;          p2 (partial process-moves-p2 moves)
;;          next-state (p2 state)]
;;     (if (= repeated-state next-state)
;;       (do
;;         (println "Found repeated state at" counter)
;;         (inc counter))
;;       (recur (inc counter) p2 (p2 next-state)))))

(defn -main []
  (println "Parsing moves...")
  (let [moves (parse-moves (slurp "resources/day16.txt"))]
    (time (process-moves moves))
    (println "Part 1:" @state)
    (time (process-moves (take (* 39 10000) (cycle moves))))
    (println "Part 2:" @state)))