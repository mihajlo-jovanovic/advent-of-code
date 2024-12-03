(ns day3.core
  (:gen-class))

(defn find-mul-occurrences [s]
  (re-seq #"mul\(\d+,\d+\)" s))

(defn multiply [s]
  (if-let [[_ a b] (re-matches #"mul\((\d+),(\d+)\)" s)]
    (* (Integer/parseInt a) (Integer/parseInt b))
    0))

(defn parse-instructions [s]
  (letfn [(process [chars enabled acc]
            (if (empty? chars)
              acc
              (cond
                (.startsWith chars "don't()")
                (recur (subs chars 7) false acc)

                (.startsWith chars "do()")
                (recur (subs chars 4) true acc)

                (and enabled (.startsWith chars "mul("))
                (let [end (.indexOf chars ")")
                      mul-str (subs chars 0 (inc end))] ;; we'll handle invalid chars in p1 function
                  (recur (subs chars (inc end)) enabled (conj acc mul-str)))

                :else
                (recur (subs chars 1) enabled acc))))]
    (process s true [])))

(defn solve [instructions]
  (reduce + (map multiply instructions)))

(defn p1 [s]
  (solve (find-mul-occurrences s)))

(defn p2 [s]
  (solve (parse-instructions s)))

(defn -main []
  (let [input (slurp "resources/input.txt")]
    (println "Part 1: " (p1 input))
    (time (println "Part 2: " (p2 input)))))