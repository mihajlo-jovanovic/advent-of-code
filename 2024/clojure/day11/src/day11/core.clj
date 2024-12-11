(ns day11.core
  (:gen-class))

(defn split-even
  "Split a number into two parts, if the number of digits is even."
  [n]
  (let [s (str n)
        l (count s)
        m (/ l 2)]
    (list (Integer/parseInt (subs s 0 m))
          (Integer/parseInt (subs s m l)))))

(defn blink
  "Rules:
   - If the stone is engraved with the number 0, it is replaced by a stone engraved with the number 1.
   - If the stone is engraved with a number that has an even number of digits, it is replaced by two stones. The left half of the digits are engraved on the new left stone, and the right half of the digits are engraved on the new right stone. (The new numbers don't keep extra leading zeroes: 1000 would become stones 10 and 0.)
   - If none of the other rules apply, the stone is replaced by a new stone; the old stone's number multiplied by 2024 is engraved on the new stone."
  [n]
  (cond
    (= n 0) 1
    (even? (count (str n))) (split-even n)
    :else (* n 2024)))

(defn blink-seq [nums]
  (flatten (map blink nums)))

(def m-count-stones
  (memoize (fn [n times] (if (zero? times)
                           1
                           (let [after-blink (blink n)]
                             (if (list? after-blink)
                               (+ (m-count-stones (first after-blink) (dec times)) (m-count-stones (second after-blink) (dec times)))
                               (m-count-stones after-blink (dec times))))))))

(defn -main
  []
  (let [stones '(1117 0 8 21078 2389032 142881 93 385)]
    (time (println "Part 2: " (reduce + (map #(m-count-stones % 75) stones))))))