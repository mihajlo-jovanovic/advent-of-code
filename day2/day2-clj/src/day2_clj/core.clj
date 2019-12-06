(ns day2-clj.core)

(defn operation
  [x]
  (if (= x 1)
    #(+ %1 %2)
    #(* %1 %2)))

(defn run
  [program]
  (letfn [(helper [program pos]
            (if (= (get program pos) 99)
              program
              (let [op1 (get program (get program (inc pos)))
                    op2 (get program (get program (+ pos 2)))]
                (recur (assoc program (get program (+ pos 3)) ((op (get program pos)) op1 op2)) (+ pos 4)))))]
    (first  (helper program 0))))

(defn get-param
  [p pos mode]
  (if (= mode 0)
    (get p (get p pos))
    (get p pos)))

(defn day5
  [p]
  (letfn [(helper [p pos acc]
            (if (= (get p pos) 99)
              acc
              (let [op (mod (get p pos) 100)
                    c (/ (- (mod (get p pos) 1000) (rem (mod (get p pos) 1000) 100)) 100)
                    b (/ (- (mod (get p pos) 10000) (rem (mod (get p pos) 10000) 1000)) 1000)
                    a (/ (- (mod (get p pos) 100000) (rem (mod (get p pos) 100000) 10000)) 10000)]
                (if (< op 3)
                  (recur (assoc p (get p (+ pos 3)) ((operation op) (get-param p (inc pos) c) (get-param p (+ pos 2) b))) (+ pos 4) acc)
                  (if (= op 3)
                    (recur (assoc p (get p (inc pos)) 1) (+ pos 2) acc)
                    (recur p (+ pos 2) (conj acc (get-param p (inc pos) c))))))))]
    (helper p 0 [])))

;; 'borrowed' from https://stackoverflow.com/questions/18246549/cartesian-product-in-clojure 
(defn cart [colls]
  (if (empty? colls)
    '(())
    (for [more (cart (rest colls))
          x (first colls)]
      (cons x more))))

(defn -main
  "Day 2 - part 2 solution"
  [p]
  (let [x (cart (list (range 100) (range 100)))
        codes (map (fn [x] (run (assoc (assoc p 1 (first x)) 2 (second x)))) x)
        i (.indexOf codes 19690720)]
    (println (get (vec x) i))))
