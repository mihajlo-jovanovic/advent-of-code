(ns day15.core
  (:gen-class))

(defn generator [factor start]
  (rem (* factor start) 2147483647))

(defn generator-p2 [factor multiple start]
  (let [next (generator factor start)]
    (if (= 0 (mod next multiple))
      next
      (recur factor multiple next))))

(defn share-last-16-bits? [a b]
  (= (rem a 65536) (rem b 65536)))

(defn solve-p1 [a b]
  (loop [a a
         b b
         count 0
         score 0]
    (if (= count 40000000)
      score
      (let [a (rem (* a 16807) 2147483647)
            b (rem (* b 48271) 2147483647)]
        (recur a b (inc count) (if (share-last-16-bits? a b) (inc score) score))))))

(defn solve-p2 [a b]
  (loop [a a
         b b
         count 0
         score 0]
    (if (= count 5000000)
      score
      (let [a (generator-p2 16807 4 a)
            b (generator-p2 48271 8 b)]
        (recur a b (inc count) (if (share-last-16-bits? a b) (inc score) score))))))

;; (count (filter share-last-16-bits? (take 40000000 (map (fn [a b] [a b]) (iterate generator-a 65) (iterate generator-b 8921)))))

(defn -main []
  (time (println "Part 1:" (solve-p1 722 354)))
  (time (println "Part 1:" (solve-p2 722 354))))