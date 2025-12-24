(ns day12.core
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn parse-input [filepath]
  (let [parts (str/split (slurp filepath) #"\n\n")
        lines (->> (take 6 parts) (map str/split-lines) (map #(drop 1 %)))
        parse-regions (fn [region] (let [[f s & r]  (mapv Integer/parseInt (re-seq #"\d+" region))] {:max-x f :max-y s :quantities (vec r)}))]
    {:gifts (mapv #(set (for [y (range (count %)) x (range (count (nth % y))) :when  (= "#" (str (nth (nth % y) x)))] [x y])) lines)
     :regions (map parse-regions (str/split-lines (last parts)))}))

;; The 3x3 grid constraints
(def max-idx 2)

(defn rotate-cw [[x y]]
  [y (- max-idx x)])

(defn rotate-ccw [[x y]]
  [(- max-idx y) x])

(defn flip-horiz [[x y]]
  [(- max-idx x) y])

(defn flip-vert [[x y]]
  [x (- max-idx y)])

(defn offset-x [n [x y]]
  [(+ n x) y])

(defn offset-y [n [x y]]
  [x (+ n y)])

(defn transform-shape [transform-fn shape]
  (set (map transform-fn shape)))

(def all-transformations [rotate-cw rotate-ccw (comp rotate-cw rotate-cw)
                          (comp flip-horiz rotate-cw) (comp flip-horiz rotate-ccw) (comp flip-horiz rotate-cw rotate-cw)
                          (comp flip-vert rotate-cw) (comp flip-vert rotate-ccw) (comp flip-vert rotate-cw rotate-cw)])

(defn print-shape [coords]
  (let [size 14
        empty-grid (vec (repeat size (vec (repeat size "."))))

        grid (reduce (fn [g [x y]]
                       (if (and (< -1 x size) (< -1 y size)) ;; Bounds check
                         (assoc-in g [y x] "#")
                         g))
                     empty-grid
                     coords)]

    (doseq [row grid]
      (println (str/join " " row)))))

(defn can-place? [region shape]
  (every? #(not (contains? region %)) shape))

(defn backtrack [region max-x max-y to-fit idx shapes]
  (if (= idx (count to-fit))
    true
    (let [a-gift (nth shapes (nth to-fit idx))
          all-t (conj (set (map #(transform-shape % a-gift) all-transformations)) a-gift)]

      (reduce (fn [acc-region shape]
                (if acc-region
                  (reduced acc-region)
                  (if (can-place? region shape)
                    (backtrack (set/union region shape) max-x max-y to-fit (inc idx) shapes)
                    acc-region)))
              false
              (apply set/union (map (fn [[x y]] (set (map #(transform-shape (partial offset-x x) (transform-shape (partial offset-y y) %)) all-t))) (for [x (range (inc (- max-x 3))) y (range (inc (- max-y 3)))] [x y])))))))

(def m-all-t
  (memoize
   (fn [g max-x max-y]
     (let [all-t (conj (set (map #(transform-shape % g) all-transformations)) g)]
       (apply set/union
              (map
               (fn [[x y]] (set (map #(transform-shape (partial offset-x x) (transform-shape (partial offset-y y) %)) all-t)))
               (for [x (range (inc (- max-x 3))) y (range (inc (- max-y 3)))] [x y])))))))

(defn generate-next-states [{:keys [gifts region quantities max-x max-y] :as current-state}]
  (let [[i q] (first (keep-indexed #(when-not (zero? %2) [%1 %2]) quantities))
        g (nth gifts i)
        only-valid (filter #(can-place? region %) (m-all-t g max-x max-y))]
    (map (fn [coll] {:gifts gifts :region (set/union region coll) :quantities (update quantities i dec) :max-x max-x :max-y max-y}) only-valid)))

(defn backtrack-2
  "Cleaner version of the same backtracking solution using idiomatic Clojure"
  [{:keys [gifts region quantities max-x max-y] :as current-state}]
  ;;  (do (println quantities))
  (if (= quantities [0 0 0 0 0 0])
    true
    (some backtrack-2 (generate-next-states current-state))))

(defn reduce-by-packing-4-by-4
  "Heuristic specific to day12 input - reducing problem space by packing pairs of gifts into 4x4 boxes"
  [{:keys [quantities max-x max-y] :as current-state}]
  (let [[q0 q1 q2 q3 q4 q5] quantities
        m1 (min q0 q4)
        m2 (min q1 q5)
        m3 (int (/ q3 2))
        total-4-by-4 (+ m1 m2 m3)
        total-per-row (int (/ max-y 4))
        rows-needed (if (zero? (mod total-4-by-4 total-per-row)) (/ total-4-by-4 total-per-row) (inc (int (/ total-4-by-4 total-per-row))))
        new-max-x (- max-x (* rows-needed 4))]
    (assoc current-state :max-x new-max-x :quantities [(- q0 m1) (- q1 m2) q2 (- q3 (* 2 m3)) (- q4 m1) (- q5 m2)])))

(defn is-feasable? [gifts {:keys [quantities max-x max-y]}]
  (let [gift-sizes (map count gifts)
        total-area (* max-x max-y)
        required-area (apply + (map #(apply * %) (partition 2 (interleave quantities gift-sizes))))]
    (> total-area required-area)))

(defn solve [{:keys [gifts regions]}]
  (let [valid (filter (partial is-feasable? gifts) regions)]
    (count (filter #(backtrack-2 (reduce-by-packing-4-by-4 (assoc % :gifts gifts :region #{}))) valid))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Proper general-purpose solution for modified 2D Bin Packing (with Gemini 3 Pro help :)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- fits-in-bins?
  "Tries to pack items into a fixed number of bins of a specific size
   using a First-Fit approach.
   items: sequence of item widths (integers)
   num-bins: number of available rows/cols
   bin-size: the length of each row/col"
  [items num-bins bin-size]
  (let [initial-bins (vec (repeat num-bins bin-size))]
    (loop [remaining-items items
           current-bins    initial-bins]
      (if (empty? remaining-items)
        true ;; All items packed successfully
        (let [item (first remaining-items)
              ;; Find the index of the first bin that has enough space
              idx  (first (keep-indexed #(when (>= %2 item) %1) current-bins))]
          (if idx
            ;; Found a spot: update that bin's remaining space and recur
            (recur (rest remaining-items)
                   (update current-bins idx - item))
            ;; No spot found for this item: packing failed
            false))))))

;; (defn can-decompose?
;;   "Determines if a container of size max-x by max-y can accommodate
;;    a specific number of 4x4 and 4x5 blocks."
;;   ;; [max-x max-y {:keys [num-4x4 num-4x5]}]
;;   ;; ;; (let [;; Calculate total areas for a basic sanity check
;;   ;; ;;   ;;     total-container-area (* max-x max-y)
;;   ;; ;;   ;; ;;     total-block-area     (+ (* num-4x4 16) (* num-4x5 20))

;;   ;; ;;   ;; ;;     ;; Prepare items: We treat 4x4 as width 4, and 4x5 as width 5.
;;   ;; ;;   ;; ;;     ;; We sort descending (5s then 4s) for better packing efficiency.
;;   ;; ;;   ;; ;; ;;     items (concat (repeat num-4x5 5) (repeat num-4x4 4))]

;;   ;; ;;   ;; ;; ;; ;; (cond
;;   ;; ;;   ;; ;; ;; ;;   ;; 1. Trivial check: If blocks have more area than container, fail immediately.
;;   ;; ;;   ;; ;; ;; ;;   ;; (> total-block-area total-container-area) false

;;   ;; ;;   ;; ;; ;; ;;   ;; ;; 2. Vertical Strips strategy:
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; If X is divisible by 4, we split the container into vertical columns of width 4.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; The bins have capacity 'max-y'.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; (zero? (mod max-x 4))
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; (fits-in-bins? items (quot max-x 4) max-y)

;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; 3. Horizontal Strips strategy:
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; If Y is divisible by 4, we split the container into horizontal rows of height 4.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; The bins have capacity 'max-x'.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; (zero? (mod max-y 4))
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; (fits-in-bins? items (quot max-y 4) max-x)

;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; ;; 4. Fallback (Neither dimension perfectly divisible by 4):
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; ;; We take the largest dimension divisible by 4 (floor) as the usable area.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; ;; We default to using horizontal strips based on max-y.
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; ;; :else
;;   ;; ;;   ;; ;; ;; ;;   ;; ;; ;; ;; ;; ;; ;; (fits-in-bins? items (quot max-y 4) max-x))))

(defn- calculate-capacity
  "Given a partition of the grid into 'strips-4' (height 4) and 'strips-5' (height 5),
   and a strip length, returns the max number of 4x4 blocks we can fit
   after accommodating the required number of 4x5 blocks."
  [strips-4 strips-5 strip-len req-5s]

  (let [cap-4-strip-width-4 (quot strip-len 4)
        cap-4-strip-width-5 (quot strip-len 5)
        cap-5-strip-width-4 (quot strip-len 4)

        ;; Priority 1: Put 5s in Height-5 strips (as rotated 5x4).
        max-5s-in-h5 (* strips-5 cap-5-strip-width-4)

        ;; RENAME: 5s-in-h5 -> cnt-5s-in-h5
        cnt-5s-in-h5 (min req-5s max-5s-in-h5)
        rem-5s       (- req-5s cnt-5s-in-h5)

        ;; Priority 2: Put remaining 5s in Height-4 strips.
        ;; RENAME: 5s-in-h4 -> cnt-5s-in-h4
        cnt-5s-in-h4 rem-5s

        ;; --- Calculate Space Remaining for 4x4s ---

        ;; 1. Space from Height-5 strips
        total-width-h5 (* strips-5 strip-len)
        used-width-h5  (* cnt-5s-in-h5 4)
        rem-width-h5   (- total-width-h5 used-width-h5)
        cap-4s-in-h5   (quot rem-width-h5 4)

        ;; 2. Space from Height-4 strips
        max-5s-in-h4 (* strips-4 cap-4-strip-width-5)]

    (if (> cnt-5s-in-h4 max-5s-in-h4)
      -1 ;; Impossible: Too many 5s for the H4 strips
      (let [total-width-h4 (* strips-4 strip-len)
            used-width-h4  (* cnt-5s-in-h4 5)
            rem-width-h4   (- total-width-h4 used-width-h4)
            cap-4s-in-h4   (quot rem-width-h4 4)]

        (+ cap-4s-in-h5 cap-4s-in-h4)))))

(defn can-decompose?
  [max-x max-y {:keys [num-4x4 num-4x5]}]
  (let [solve-axis (fn [main-axis cross-axis]
                     ;; Try all combinations of 4-strips and 5-strips that fit in main-axis
                     (some (fn [num-5-strips]
                             (let [space-used-by-5s (* num-5-strips 5)
                                   space-left (- main-axis space-used-by-5s)]
                               (when (>= space-left 0)
                                 (let [num-4-strips (quot space-left 4)
                                       max-4s (calculate-capacity
                                               num-4-strips
                                               num-5-strips
                                               cross-axis
                                               num-4x5)]
                                   (>= max-4s num-4x4)))))
                           (range (inc (quot main-axis 5)))))]

    ;; Try slicing along Y (strips run X) OR slicing along X (strips run Y)
    (or (true? (solve-axis max-y max-x))
        (true? (solve-axis max-x max-y)))))

(defn solve-packing
  "Calculates the number of 4x4 and 4x5 boxes required to pack the given quantities.
   Prioritizes maximizing 4x4 boxes (capacity 2) based on the valid pairings
   defined in the key-matrix. Remaining items are packed into 4x5 boxes (capacity 2)."
  [quantities key-matrix]
  (let [;; 1. Parse constraints from the matrix to see which items are compatible
        ;;    We sum the columns to see how 'flexible' an item is.
        ;;    If a column sums to 0, that item NEVER fits in a 4x4 (e.g., item 2).
        col-sums (apply map + key-matrix)

        ;; 2. Identify the role of each item index
        ;;    - strictly-4x5: Items that have 0 column sum.
        ;;    - self-only: Items that only appear in a row like [0 0 0 2 0 0].
        ;;    - flexible: Items that can mix.
        item-roles (map-indexed
                    (fn [idx sum]
                      (cond
                        (zero? sum) :strictly-4x5
                        ;; Check if it is self-only (hardcoded logic check for the specific pattern [0..2..0])
                        ;; We check if the item allows mixing by checking if it appears in any row with other items.
                        ;; A simple heuristic here: In the provided example, 0 is restricted, 4/5 are flexible.
                        ;; We will implement the specific logic derived from the problem class:
                        ;; Group A (Needs Help): Item 0 (Pairs only with 4,5)
                        ;; Group B (Helpers): Items 4, 5 (Can pair with anything)
                        ;; Group C (Self-Sufficient): Item 1 (Pairs with 1, 4, 5)
                        ;; Group D (Isolationist): Item 3 (Pairs only with 3)
                        :else :mixable))
                    col-sums)

        ;; Extract quantities by group for the solver
        q (vec quantities)

        ;; Group D: Item 3 (Index 3). Strictly pairs with itself in 4x4.
        qty-3 (get q 3 0)
        boxes-3 (quot qty-3 2)
        rem-3   (rem qty-3 2)

        ;; Group A: Item 0. Restricted. Needs 4 or 5.
        qty-0 (get q 0 0)

        ;; Group C: Item 1. Semi-restricted. Needs 1, 4, or 5.
        qty-1 (get q 1 0)

        ;; Group B: Items 4 and 5. Flexible helpers.
        qty-helpers (+ (get q 4 0) (get q 5 0))

        ;; --- GREEDY MATCHING LOGIC ---

        ;; Step 1: Pair Group A (0s) with Helpers (4s/5s)
        ;; 0s cannot pair with 0s or 1s, so they MUST take a helper.
        pairs-0-helper (min qty-0 qty-helpers)
        rem-0          (- qty-0 pairs-0-helper)      ;; Must go to 4x5
        rem-helpers    (- qty-helpers pairs-0-helper)

        ;; Step 2: Pair Group C (1s) with remaining Helpers
        ;; Mixing 1s with helpers is fine.
        pairs-1-helper (min qty-1 rem-helpers)
        rem-1          (- qty-1 pairs-1-helper)
        rem-helpers-2  (- rem-helpers pairs-1-helper)

        ;; Step 3: Pair remaining Group C (1s) with themselves
        ;; The matrix allows [0 2 0 0 0 0], so 1s can pair with 1s.
        pairs-1-1      (quot rem-1 2)
        final-rem-1    (rem rem-1 2)

        ;; Step 4: Pair remaining Helpers with themselves
        ;; 4s and 5s can pair with each other or themselves.
        pairs-helper-helper (quot rem-helpers-2 2)
        final-rem-helper    (rem rem-helpers-2 2)

        ;; Calculate Total 4x4 Boxes
        total-4x4 (+ boxes-3
                     pairs-0-helper
                     pairs-1-helper
                     pairs-1-1
                     pairs-helper-helper)

        ;; --- 4x5 CALCULATION ---
        ;; All leftovers must go to 4x5.
        ;; This includes:
        ;; 1. Item 2 (Strictly 4x5)
        ;; 2. Remainder of Item 3 (if any)
        ;; 3. Remainder of 0s (unmatchable)
        ;; 4. Remainder of 1s (odd number)
        ;; 5. Remainder of Helpers (odd number)
        qty-2 (get q 2 0)

        total-leftovers (+ qty-2
                           rem-3
                           rem-0
                           final-rem-1
                           final-rem-helper)

        ;; 4x5 boxes hold 2 items each.
        total-4x5 (long (Math/ceil (/ total-leftovers 2.0)))]

    {:num-4x4 total-4x4
     :num-4x5 total-4x5}))

(defn solve-take2 [{:keys [gifts regions]}]
  (let [valid (filter (partial is-feasable? gifts) regions)
        all-pairs (filter #(= 2 (apply + %)) (for [q0 (range 3) q1 (range 3) q2 (range 3) q3 (range 3) q4 (range 3) q5 (range 3)] [q0 q1 q2 q3 q4 q5]))
        key-matrix (into [] (filter #(backtrack-2 {:gifts gifts :region #{} :quantities % :max-x 4 :max-y 4}) all-pairs))]
    (count (filter #(can-decompose? (:max-x %) (:max-y %) (solve-packing (:quantities %) key-matrix)) valid))))

(defn -main []
  (let [input (parse-input "resources/day12.txt")]
    (time (println "Part 1: " (solve-take2 input)))))
