(ns day9.core)

;; (def state {:score 0 :depth 0 :garbage false :exclamation false :count 0})

(defn update-state [state char]
  (if (:exclamation state)
    (assoc state :exclamation false)
    (cond
      (= char \{) (if (not (:garbage state)) (update state :depth inc) (update state :count inc))
      (= char \}) (if (not (:garbage state)) (-> state
                                                 (update :score + (:depth state))
                                                 (update :depth dec))
                      (update state :count inc))
      (= char \<) (if (:garbage state) (update state :count inc) (assoc state :garbage true))
      (= char \>) (assoc state :garbage false)
      (= char \!) (if (:garbage state) (assoc state :exclamation true) state)
      :else (if (:garbage state) (update state :count inc) state))))

(defn process-stream [initial-state char-stream]
  (:count (reduce update-state initial-state char-stream)))