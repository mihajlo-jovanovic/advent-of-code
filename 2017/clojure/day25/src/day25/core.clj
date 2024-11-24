(ns day25.core
  (:gen-class))

(def configuration {:pos 0 :tape {} :state :A})

;; (defn do-step [configuration]
;;   (let [tape (:tape configuration)
;;         pos (:pos configuration)
;;         new-value (if (= 1 (get tape (:pos configuration))) 0 1)]
;;     (cond
;;       (= (:state configuration) :A) (assoc configuration :pos (if (= 1 (get tape pos)) (dec pos) (inc pos)) :tape (assoc tape pos new-value) :state :B)
;;       (= (:state configuration) :B) (assoc configuration :pos (if (= 1 (get tape pos)) (inc pos) (dec pos)) :tape (assoc tape pos 1) :state :A))))

(defn transition [state current-value]
  "Returns the new state, value to write, direction to move, and next state based on the current state and value."
  (case state
    :A (if (= current-value 0)
         [1 :right :B]
         [0 :left :C])
    :B (if (= current-value 0)
         [1 :left :A]
         [1 :right :D])
    :C (if (= current-value 0)
         [1 :right :A]
         [0 :left :E])
    :D (if (= current-value 0)
         [1 :right :A]
         [0 :right :B])
    :E (if (= current-value 0)
         [1 :left :F]
         [1 :left :C])
    :F (if (= current-value 0)
         [1 :right :D]
         [1 :right :A])))

(defn update-tape [tape pos value]
  "Updates the tape by writing the value at the specified position."
  (assoc tape pos value))

(defn move-pos [pos direction]
  "Returns the new position after moving in the specified direction."
  (case direction
    :left (dec pos)
    :right (inc pos)))

(defn do-step [configuration]
  "Performs a single step of the state machine."
  (let [{:keys [pos tape state]} configuration
        current-value (get tape pos 0)  ; Default to 0 if the position is not yet in the tape
        [write-value direction next-state] (transition state current-value)]
    {:pos (move-pos pos direction)
     :tape (update-tape tape pos write-value)
     :state next-state}))

(defn -main []
  (println "Solution: " (count (filter (fn [[_ v]] (= 1 v)) (:tape (last (take 12173598 (iterate do-step configuration))))))))
