(ns day2-clj.core)

(defn op
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
