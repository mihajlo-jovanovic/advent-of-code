(ns day10.core)

;; (def state {:current-position 0
;;             :skip-size 0
;;             :list (vec (range 0 5))})

(defn update-state [state length]
  (let [list (:list state)
        current-position (:current-position state)
        skip-size (:skip-size state)
        list-length (count list)
        ;; Extract the sublist to reverse
        sublist-to-reverse (subvec list current-position (+ current-position length))
        ;; Reverse the sublist
        reversed-sublist (reverse sublist-to-reverse)
        ;; Create the new list by concatenating the reversed sublist and the remaining elements
        new-list (vec (concat (vec reversed-sublist) (subvec list (+ current-position length) list-length)))
        new-state {:current-position (+ current-position length skip-size)
                   :skip-size (inc skip-size)
                   :list new-list}]
    new-state))

;; Shifts the elements of the list to the left by n positions.
;; Elements that are shifted out of the list are reinserted at the end.
(defn shift-list [list n]
  (let [list-length (count list)
        n (mod n list-length)]
    (vec (concat (subvec list n list-length) (subvec list 0 n)))))

(defn calculate-new-position [state length list-length]
  (mod (+ (:current-position state) length (:skip-size state)) list-length))

(defn update-state2 [state length]
  (if (zero? (:current-position state))
    (update-state state length)
    (let [remaining-length (- (count (:list state)) (:current-position state))
          list (:list state)
          new-list (shift-list list (:current-position state))
          updated-state (update-state (assoc state :list new-list :current-position 0) length)
          shifted-back-list (shift-list (:list updated-state) remaining-length)]
      (-> updated-state
          (assoc :list shifted-back-list)
          (assoc :current-position (calculate-new-position state length (count shifted-back-list)))))))

(defn knot-hash [lengths]
  (let [initial-state {:current-position 0
                       :skip-size 0
                       :list (vec (range 0 256))}
        final-state (transduce (map identity) update-state2 initial-state lengths)]
    (* (get (:list final-state) 0) (get (:list final-state) 1))))

(defn ascii->list [input]
  (map int (seq input)))

(defn num-hex [n]
  (if (< n 16)
    (str "0" (Integer/toHexString n))
    (Integer/toHexString n)))

(defn bitwise-xor [coll]
  (apply bit-xor coll))

(defn knot-hash-p2 [lengths]
  (let [initial-state {:current-position 0
                       :skip-size 0
                       :list (vec (range 0 256))}
        final-state (reduce update-state2 initial-state lengths)]
    (:list final-state)))

(defn solve-p2 [s]
  (let [l (concat (ascii->list s) [17 31 73 47 23])
        repeated (flatten (repeat 64 l))
        after-rounds (knot-hash-p2 repeated)]
    (apply str (map num-hex (map bitwise-xor (partition 16 after-rounds))))))