(ns day17.core
  (:gen-class))

;; Helper function to insert an element at a given index in a list
(defn list-insert [lst elem index]
  (let [[left right] (split-at index lst)]
    (concat left (list elem) right)))

(defn spinlock [num-of-steps state]
  (let [{:keys [index buffer]} state
        len (count buffer)
        new-value (inc (nth buffer index))
        new-index (inc (mod (+ index num-of-steps) len))]
    {:buffer (list-insert buffer new-value new-index)
     :index new-index}))

;; Part 2: no need to actually build the buffer
(def state2 {:index 0 :value 1 :value-after-zero 0})

(defn spinlock-optimized [num-of-steps state]
  (let [{:keys [index value value-after-zero]} state
        new-value (inc value)
        new-index (inc (mod (+ index num-of-steps) value))
        new-value-after-zero (if (= new-index 1) value value-after-zero)]
    {:value new-value
     :index new-index
     :value-after-zero new-value-after-zero}))

(defn -main []
  (let [num-of-steps 337
        spinlock (partial spinlock-optimized num-of-steps)]
    (time
     (println "Part 2:"
              (->> (iterate spinlock state2)
                   (take 50000001)
                   (last)
                   :value-after-zero)))))