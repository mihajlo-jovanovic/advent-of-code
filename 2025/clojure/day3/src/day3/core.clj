(ns day3.core
  (:require [clojure.string :as s]))

(defn parse-input [filepath]
  (->> (slurp filepath)
       (s/split-lines)
       (mapv (fn [line] (mapv #(Character/digit % 10) line)))))

(defn find-pair-in-window [v start-idx step-idx]
  (let [len (count v)
        remaining-steps (- 5 step-idx)
        
        ;; First number window
        ;; We ignore (1 + 2 * remaining) from the end
        ignore-suffix-1 (inc (* 2 remaining-steps))
        end-idx-1 (- len ignore-suffix-1)
        
        ;; Find max in the first window
        window-1 (subvec v start-idx end-idx-1)
        first-val (apply max window-1)
        ;; Find index of first-val relative to start-idx to get absolute index
        ;; Note: .indexOf returns index in the subvec, so we add start-idx
        first-rel-idx (.indexOf window-1 first-val)
        first-abs-idx (+ start-idx first-rel-idx)
        
        ;; Second number window
        ;; Starts after the first number
        ;; We ignore (2 * remaining) from the end
        start-idx-2 (inc first-abs-idx)
        ignore-suffix-2 (* 2 remaining-steps)
        end-idx-2 (- len ignore-suffix-2)
        
        ;; Find max in the second window
        window-2 (subvec v start-idx-2 end-idx-2)
        second-val (apply max window-2)
        ;; Find index of second-val relative to start-idx-2
        second-rel-idx (.indexOf window-2 second-val)
        second-abs-idx (+ start-idx-2 second-rel-idx)]
    
    {:val (+ (* first-val 10) second-val)
     :next-start (inc second-abs-idx)}))

(defn p2 [input]
  (let [process-line (fn [digits]
                       (loop [i 0
                              offset 0
                              acc-str ""]
                         (if (= i 6)
                           (Long/parseLong acc-str)
                           (let [{:keys [val next-start]} (find-pair-in-window digits offset i)]
                             (recur (inc i)
                                    next-start
                                    (str acc-str val))))))]
    (->> input
         (map process-line)
         (reduce +))))
