(ns day10.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-line [parts]
  (let [expected (first parts)
        expected-vec (into [] (map first (filter #(= "#" (second %)) (map-indexed vector  (re-seq #"[.#]" expected)))))
        buttons (drop-last (rest parts))
        buttons-vec (read-string (apply str (concat "[" (apply concat buttons) "]")))]
    [expected-vec buttons-vec]))

(defn parse-line-p2 [parts]
  (let [expected (last parts)
        expected-vec (into [] (map Integer/parseInt (re-seq #"\d+" expected)))
        buttons (drop-last (rest parts))
        buttons-vec (read-string (apply str (concat "[" (apply concat buttons) "]")))]
    [expected-vec buttons-vec]))

(defn parse-input [filepath]
  (let [lines (str/split-lines (slurp filepath))]
    (->> lines
         (map #(str/split % #" "))
         (map parse-line-p2))))

(defn match? [expected buttons presses]
  (= expected (sort (map first (filter #(odd? (second %)) (frequencies (flatten (apply concat
                                                                                       (for [i (range (count buttons))]
                                                                                         (repeat (nth presses i) (nth buttons i)))))))))))

(defn parse-constraints [expected buttons]
  (let [extract-indices (fn [btns idx] (mapv first (filter (fn [[i coll]] (contains? (set coll) idx)) (map-indexed vector btns))))]
    (partition 2 (interleave  (map #(extract-indices buttons %) (range (count expected))) expected))))

(defn match-p2? [expected buttons presses]
  (= expected (map second  (sort-by first (frequencies (flatten (apply concat
                                                                       (for [i (range (count buttons))]
                                                                         (repeat (nth presses i) (nth buttons i))))))))))
(defn cartesian-product-reduce [colls]
  (reduce
   (fn [acc new-col]
      ;; For every existing tuple (a) and every new item (b), combine them
     (for [a acc
           b new-col]
       (conj a b)))
   [[]]
   colls))

(defn p1 [[expected buttons]]
  (apply min (map (fn [coll] (reduce + coll))  (filter #(match? expected buttons %) (cartesian-product-reduce (repeat (count buttons) (range 2)))))))

(defn p2 [expected buttons]
  (apply min (map (fn [coll] (reduce + coll))  (filter #(match-p2? expected buttons %) (cartesian-product-reduce (repeat (count buttons) (range (inc (apply max expected)))))))))

;; (map #(map first (filter (fn [[idx button]] (contains? (into #{} button) %)) (map-indexed vector buttons))) (range 4))

(defn increment-last [seq]
  (let [reversed (reverse seq)
        head (first reversed)]
    (reverse (cons (inc head) (rest reversed)))))

(defn increment-last-vec [v]
  (update v (dec (count v)) inc))

(defn generate-next-state [m max-value seq]
  (if (< (count seq) m)
    (conj seq 0)
    (if (< (last seq) max-value)
      (increment-last-vec seq)
      (let [reversed (reverse seq)
            trimmed (drop-while #(= % max-value) reversed)]
        (if (empty? trimmed)
          nil
          (into [] (reverse (cons (inc (first trimmed)) (rest trimmed)))))))))

(defn fails-constraints? [v]
  (let [index (count v)]
    (if (> index 3)
      (if (not= 7 (+ (get v 0) (get v 1) (get v 3)))
        true
        (if (> index 4)
          (if (not= 4 (+ (get v 2) (get v 3) (get v 4)))
            true
            (if (> index 5)
              (if (not= 3 (+ (get v 4) (get v 5)))
                true
                (if (not= 5 (+ (get v 1) (get v 5)))
                  true
                  false))))))
      false)))

(def input (first (parse-input "resources/sample.txt")))
(def joltage (first input))
(def buttons (second input))
(def m 6)
(def max-value (apply max joltage))

(defn solution? [coll]
  (if (not= m (count coll))
    false
    (match-p2? joltage buttons coll)))

(defn solve-puzzle [coll]
  (if (solution? coll)
    coll
    (some solve-puzzle (filter #(not (fails-constraints? %)) (take 10000 (iterate (partial generate-next-state 6 7) coll))))))

(defn solve-puzzle2 [coll]
  (if (solution? coll)
    coll
    (if (fails-constraints? coll)
      nil
      (for [i (range (inc max-value))]
        (solve-puzzle2 (conj coll i))))))

;; --- 1. HELPER: Pre-process Constraints ---
(defn analyze-constraints [m constraints-data]
  (let [;; Find the highest target sum to set a safe search upper bound
        global-max (apply max (map second constraints-data))

        ;; Map 1: Constraints triggering at a specific index (for final validation)
        ;; Key: max-index, Value: list of constraint definitions
        by-trigger (reduce (fn [acc [indices target]]
                             (let [trigger (apply max indices)]
                               (update acc trigger conj {:indices indices :target target})))
                           {}
                           constraints-data)

        ;; Map 2: Constraints involving a specific index (for Lookahead/Forward Checking)
        ;; Key: any-index, Value: list of constraint definitions
        by-participant (reduce (fn [acc [indices target]]
                                 (reduce (fn [inner-acc idx]
                                           (update inner-acc idx conj {:indices (set indices) :target target}))
                                         acc
                                         indices))
                               {}
                               constraints-data)]
    {:global-max     global-max
     :by-trigger     by-trigger
     :by-participant by-participant}))

;; --- 2. LOGIC: Forward Checking (Is Feasible?) ---
(defn feasible? [current-seq current-idx val constraints-map global-max]
  ;; We temporarily append the value to check feasibility
  (let [temp-seq (conj current-seq val)
        relevant-constraints (get constraints-map current-idx)]

    (loop [cs relevant-constraints]
      (if (empty? cs)
        true ;; All constraints passed lookahead
        (let [{:keys [indices target]} (first cs)
              ;; Calculate state of this constraint
              state (reduce (fn [acc idx]
                              (if (<= idx current-idx)
                                ;; Index is known (or is the one we just picked)
                                (update acc :sum + (nth temp-seq idx))
                                ;; Index is in the future
                                (update acc :slots inc)))
                            {:sum 0 :slots 0}
                            indices)
              current-sum (:sum state)
              remaining-slots (:slots state)

              ;; Bounds Calculation
              min-possible (+ current-sum (* remaining-slots 0))
              max-possible (+ current-sum (* remaining-slots global-max))]

          ;; Check if target is unreachable
          (if (or (> min-possible target)
                  (< max-possible target))
            false ;; Prune
            (recur (rest cs))))))))

;; --- 3. RECURSIVE SOLVER ---
(defn solve-recursive [m constraints-data]
  (let [{:keys [global-max by-trigger by-participant]} (analyze-constraints m constraints-data)]

    ;; Inner recursive function
    ;; Returns: {:seq [1 2 3...] :sum 10} or the 'best' passed in if no better found
    (defn backtrack [current-idx current-seq current-sum best-sol]
      (let [min-total-sum (:sum best-sol)]

        ;; PRUNING 1: Cost
        (if (>= current-sum min-total-sum)
          best-sol

          ;; CHECK: Did we just finish a constraint group? (Legacy Check)
          (let [last-idx (dec current-idx)
                triggered (get by-trigger last-idx)]
            (if (and (seq triggered)
                     (not-every? (fn [{:keys [indices target]}]
                                   (= (reduce + (map #(nth current-seq %) indices)) target))
                                 triggered))
              best-sol ;; Constraint violation, return current best (effectively pruning this branch)

              ;; BASE CASE: Sequence Full
              (if (= current-idx m)
                (if (< current-sum min-total-sum)
                  {:seq current-seq :sum current-sum}
                  best-sol)

                ;; RECURSION LOOP
                ;; We use 'reduce' to carry the 'best-sol' through the iterations of 0..global-max
                (reduce (fn [acc-best val]
                          ;; PRUNING 2: Lookahead
                          (if (feasible? current-seq current-idx val by-participant global-max)
                            ;; If feasible, dive deeper
                            (backtrack (inc current-idx)
                                       (conj current-seq val)
                                       (+ current-sum val)
                                       acc-best)
                            ;; If not feasible, skip
                            acc-best))
                        best-sol
                        (range (inc global-max)))))))))

    ;; Start recursion
    (backtrack 0 [] 0 {:seq nil :sum Double/POSITIVE_INFINITY})))

;; ;; ;; ;; ;; ;; --- 4. EXECUTION ---
;; ;; ;; ;; (defn -main []
;; ;; ;;   (let [input (parse-input "resources/day10.txt")]
;; ;;     (doseq [i input]
;;       (println (parse-constraints (first i) (second i))))))

;; ;; ;;    (map #(vector (parse-constraints (first %) (second %)) (count (second %))) input)

;;    (println "I don't do much yet..." (count input))

;; (reduce + (map :sum (map (fn [[c m]] (solve-recursive m c)) (map #(vector (parse-constraints (first %) (second %)) (count (second %))) input))))

;; Run it
;; (-main)
;;
;; (defn -main []
;;  (println "Part 1: " (reduce + (map (fn [[expected buttons]] (p1 expected buttons)) (parse-input "resources/day10.txt"))))
;; --- 1. MATRIX MATH CORE ---
(defn to-matrix [num-vars constraints]
  ;; Convert constraints to Augmented Matrix
  (mapv (fn [[indices target]]
          (let [row (vec (repeat num-vars 0))]
            (conj (reduce #(assoc %1 %2 1) row indices) target)))
        constraints))

(defn normalize-row [row col-idx]
  (let [pivot-val (nth row col-idx)]
    (if (zero? pivot-val) row
        (mapv #(/ % pivot-val) row))))

(defn eliminate-row [target-row source-row col-idx]
  (let [factor (nth target-row col-idx)]
    (if (zero? factor) target-row
        (mapv - target-row (mapv #(* % factor) source-row)))))

(defn rref [m matrix]
  (loop [mat matrix r 0 c 0]
    (if (or (>= r (count mat)) (>= c m))
      mat
      (let [pivot-idx (first (keep-indexed (fn [i row]
                                             (when (and (>= i r) (not (zero? (nth row c)))) i))
                                           mat))]
        (if-not pivot-idx
          (recur mat r (inc c))
          (let [r1 (nth mat r)
                r2 (nth mat pivot-idx)
                mat-swapped (assoc mat r r2 pivot-idx r1)
                row-norm (normalize-row (nth mat-swapped r) c)
                mat-norm (assoc mat-swapped r row-norm)
                mat-final (vec (map-indexed (fn [i row]
                                              (if (= i r) row (eliminate-row row row-norm c)))
                                            mat-norm))]
            (recur mat-final (inc r) (inc c))))))))

;; --- 2. DYNAMIC BOUNDS CALCULATOR ---

(defn get-dynamic-bounds [m free-vars solved-matrix pivots]
  ;; For every Free Variable, calculate its max valid value based on Pivot equations.
  ;; Eq: Pivot = Constant - (Coeff * FreeVar)
  ;; Constraint: Pivot >= 0
  ;; Therefore: Constant - (Coeff * FreeVar) >= 0  =>  Constant >= Coeff * FreeVar

  (reduce (fn [bounds fv]
            (let [implied-limit
                  (reduce (fn [curr-min pivot-col]
                            (let [row-idx (get pivots pivot-col)
                                  row (nth solved-matrix row-idx)
                                  constant (last row)
                                  coeff (nth row fv)]
                              (if (pos? coeff)
                                ;; Constant >= Coeff * FV  =>  FV <= Constant / Coeff
                                (min curr-min (int (Math/floor (/ constant coeff))))
                                curr-min)))
                          200 ;; Default safety cap if no constraints limit it
                          (keys pivots))]
              (assoc bounds fv implied-limit)))
          {}
          free-vars))

;; --- 3. SINGLE PROBLEM SOLVER ---
(defn whole? [n]
  ;; Returns true for 12, 12.0, 12/1, but false for 12.5 or 12/5
  (zero? (mod n 1)))

(defn solve-one-case [constraints]
  (let [;; 1. Determine dynamic size (m)
        indices (mapcat first constraints)
        m (inc (if (seq indices) (apply max indices) 0))

        ;; 2. RREF
        matrix (to-matrix m constraints)
        solved-matrix (rref m matrix)

        ;; 3. Identify Variables
        pivots (reduce (fn [acc [r-idx row]]
                         (let [c-idx (first (keep-indexed #(when (= %2 1) %1) (butlast row)))]
                           (if c-idx (assoc acc c-idx r-idx) acc)))
                       {}
                       (map-indexed vector solved-matrix))
        free-vars (remove (set (keys pivots)) (range m))

        ;; 4. Calculate Search Bounds (Optimization)
        bounds-map (get-dynamic-bounds m free-vars solved-matrix pivots)]

    ;; 5. Search Free Variables (Recursive)
    (letfn [(search [assigned-free-vars]
              (if (= (count assigned-free-vars) (count free-vars))
                ;; Base Case: All free vars picked. Compute Pivots.
                (let [full-seq (reduce (fn [seq-acc pivot-idx]
                                         (let [row-idx (get pivots pivot-idx)
                                               row (nth solved-matrix row-idx)
                                               constant (last row)
                                               val (- constant
                                                      (reduce + (map (fn [fv-idx fv-val]
                                                                       (* (nth row fv-idx) fv-val))
                                                                     (range (count free-vars))
                                                                     assigned-free-vars)))]
                                           (assoc seq-acc pivot-idx val)))
                                       (vec (map (zipmap free-vars assigned-free-vars) (range m)))
                                       (keys pivots))]
                  ;; Check non-negative integers
                  (if (every? (fn [n] (and (whole? n) (>= n 0))) full-seq)
                    {:seq full-seq :sum (reduce + full-seq)}
                    nil))

                ;; Recursive Step: Loop this free var within bounds
                (let [fv-idx (nth free-vars (count assigned-free-vars))
                      limit (get bounds-map fv-idx)]
                  (->> (range (inc limit))
                       (keep (fn [val] (search (conj assigned-free-vars val))))
                       (sort-by :sum) ;; Minimization strategy
                       first))))]

      (search []))))

;; --- 4. MAIN RUNNER ---

;; (defn -main []
;;   ;; (let [all-problems
;;   ;;   ;;     [ '(([2 4 5] 44) ([0 3 5] 35) ([3 4 6] 48) ([0 1 2 3] 43) ([1 3] 24) ([2 4 6] 44))
;;   ;;   ;; ;;       '(([2 3 9] 36) ([0 2 3 4 6 7 9] 103) ([1 2 3 4 7 9] 79) ([2 4 5 6 7 9] 85) ([4 5 6 7 9] 68) ([0 1 2 3 9] 62) ([0 5 7 9] 43) ([0 3 4 5] 60) ([0 2 7 8] 168))
;;   ;;   ;; ;; ;;       ;; ... paste your full list here ...
;;   ;;   ;; ;; ;; ;;       ]]

;;   ;;   ;; ;; ;; ;; ;; (doseq [[idx prob] (map-indexed vector all-problems)]
;;   ;;   ;; ;; ;; ;; ;;   ;; (println (str "Problem " (inc idx) ": Solving..."))
;;   ;;   ;; ;; ;; ;; ;;   ;; ;; (let [result (solve-one-case prob)]
;;   ;;   ;; ;; ;; ;; ;;   ;; ;;   ;; (if result
;;   ;;   ;; ;; ;; ;; ;;   ;; ;;   ;;   ;; (println (str "  -> Solved! Min Sum: " (:sum result) " | Seq: " (:seq result)))
;;   ;;   ;; ;; ;; ;; ;;   ;; ;;   ;;   ;; ;; (println "  -> No integer solution found."))))))

;; --- 1. ROBUST NUMBER CHECKING ---
(defn whole? [n]
  ;; Returns true for 12, 12.0, 12/1. False for 12.5.
  (zero? (mod n 1)))

;; --- 2. MATRIX MATH (Gaussian Elimination) ---
(defn to-matrix [num-vars constraints]
  (mapv (fn [[indices target]]
          (let [row (vec (repeat num-vars 0))]
            (conj (reduce #(assoc %1 %2 1) row indices) target)))
        constraints))

(defn normalize-row [row col-idx]
  (let [pivot-val (nth row col-idx)]
    (if (zero? pivot-val) row
        (mapv #(/ % pivot-val) row))))

(defn eliminate-row [target-row source-row col-idx]
  (let [factor (nth target-row col-idx)]
    (if (zero? factor) target-row
        (mapv - target-row (mapv #(* % factor) source-row)))))

(defn rref [m matrix]
  (loop [mat matrix r 0 c 0]
    (if (or (>= r (count mat)) (>= c m))
      mat
      (let [pivot-idx (first (keep-indexed (fn [i row]
                                             (when (and (>= i r) (not (zero? (nth row c)))) i))
                                           mat))]
        (if-not pivot-idx
          (recur mat r (inc c))
          (let [r1 (nth mat r)
                r2 (nth mat pivot-idx)
                mat-swapped (assoc mat r r2 pivot-idx r1)
                row-norm (normalize-row (nth mat-swapped r) c)
                mat-norm (assoc mat-swapped r row-norm)
                mat-final (vec (map-indexed (fn [i row]
                                              (if (= i r) row (eliminate-row row row-norm c)))
                                            mat-norm))]
            (recur mat-final (inc r) (inc c))))))))

;; --- 3. SOLVER ---

(defn solve-one-case [constraints]
  (let [;; 1. Determine size
        indices (mapcat first constraints)
        m (inc (if (seq indices) (apply max indices) 0))

        ;; 2. Linear Algebra (RREF)
        matrix (to-matrix m constraints)
        solved-matrix (rref m matrix)

        ;; 3. Identify Pivot vs Free Variables
        pivots (reduce (fn [acc [r-idx row]]
                         (let [c-idx (first (keep-indexed #(when (= %2 1) %1) (butlast row)))]
                           (if c-idx (assoc acc c-idx r-idx) acc)))
                       {}
                       (map-indexed vector solved-matrix))
        free-vars (vec (remove (set (keys pivots)) (range m)))]

    ;; 4. Search Free Variables (Simple Brute Force 0..100)
    ;; We removed the complex 'bounds' logic. We just check 0..100.
    (letfn [(search [assigned-vals] (if (= (count assigned-vals) (count free-vars))

                ;; Base Case: All free vars assigned. Calculate Dependents.
                                      (let [free-map (zipmap free-vars assigned-vals)
                                            full-seq (reduce (fn [seq-acc pivot-idx]
                                                               (let [row-idx (get pivots pivot-idx)
                                                                     row (nth solved-matrix row-idx)
                                                                     constant (last row)
                                               ;; Pivot = Constant - sum(coeff * free_val)
                                                                     val (- constant
                                                                            (reduce + (map (fn [fv]
                                                                                             (* (nth row fv) (get free-map fv)))
                                                                                           free-vars)))]
                                                                 (assoc seq-acc pivot-idx val)))
                                                             (vec (map (fn [i] (get free-map i 0)) (range m)))
                                                             (keys pivots))]

                  ;; Validate: Must be non-negative integers
                                        (if (every? (fn [n] (and (whole? n) (>= n 0))) full-seq)
                                          {:seq (mapv int full-seq) :sum (int (reduce + full-seq))}
                                          nil))

                ;; Recursive Step: Loop next free var 0..100
                ;; We use `keep` to find the first non-nil result in the sequence
                                      (->> (range 131)
                                           (keep (fn [val] (search (conj assigned-vals val))))
                                           (sort-by :sum)
                                           first)))]

      (search []))))

;; --- 4. RUNNER ---

(defn -main []
  (let [all-problems (map (fn [[exp btns]] (parse-constraints exp btns)) (parse-input "resources/day10.txt"))]

    (println "Part 2: " (time (reduce + (map :sum (map solve-one-case all-problems)))))

    ;; ;; (doseq [[idx prob] (map-indexed vector all-problems)]
    ;; ;;   (println (str "Problem " (inc idx) ": Solving..."))
    ;; ;;   (let [result (solve-one-case prob)]
    ;; ;;     (if result
    ;; ;;       (println (str "  -> Solved! Min Sum: " (:sum result) " | Seq: " (:seq result)))
    ;;       (println "  -> No integer solution found (in range 0-100)."))))
    ))
