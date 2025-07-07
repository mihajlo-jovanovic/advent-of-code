(ns day12.core
  (:gen-class))

;; (def rules-map (apply merge (map #(hash-map (string-to-bits (first (clojure.string/split % #" => "))) (if (= "#" (second (clojure.string/split % #" => "))) 1 0)) rules)))
;; (def init-state-vc (vec (concat (into [] (take 10 (repeat 0))) (string-to-bits init-state) (into [] (take 105 (repeat 0))))))

(defn string-to-bits [s]
  (vec
   (map (fn [c]
          (if (= c \#) 1 0))
        s)))

(defn bits-to-string [coll]
  (apply str
         (map (fn [el]
                (if (= el 1) \# \.)) coll)))
(defn next-generation
  [rules-map init-state]
  (into [] (map #(rules-map %) (partition 5 1 (into [0 0] (conj (conj init-state 0) 0))))))

(defn part1
  [rules-map init-state]
  (let [step (partial next-generation rules-map)]
    (reduce + (map second (filter #(= 1 (first %)) (map vector (last (take 21 (iterate step init-state))) (range -10 119)))))))

(defn part2
  [rules-map init-state]
  (loop [g (next-generation rules-map init-state)
         c 140]
    (println (bits-to-string g))
    (if (= c 0)
      "done"
      (recur (next-generation rules-map g) (dec c)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Part 2: " (reduce + (range 49999999913 (+ 49999999913 186)))))