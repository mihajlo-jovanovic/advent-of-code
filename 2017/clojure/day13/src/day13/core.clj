(ns day13.core
  (:gen-class)
  (:require
   [clojure.string :as s]))

(defn parse-input [filename]
  (map (fn [line]
         (let [parts (s/split line #": ")]
           (map #(Integer/parseInt %) parts)))
       (->
        (slurp filename)
        (s/split-lines))))

(defn p1 [firewall]
  (reduce (fn [acc l]
            (if (= 0 (mod (first l) (* 2 (dec (second l)))))
              (+ acc (* (first l) (second l)))
              acc))
          0
          firewall))

(defn p2 [coll]
  (loop [delay 0]
    (if (seq (filter #(= 0 (mod (+ delay (first %)) (* 2 (dec (second %))))) coll))
      (recur (inc delay))
      delay)))

(defn -main []
  (let [input (parse-input "resources/day13.txt")]
    (println (p1 input))
    (time (println (p2 input)))))