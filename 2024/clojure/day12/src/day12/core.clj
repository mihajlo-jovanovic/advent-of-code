(ns day12.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.set :as set]))

(defn parse-grid
  [filename]
  (let [lines (s/split-lines (slurp filename))]
    {:grid (->> (for [y (range (count lines))
                      x (range (count (nth lines y)))
                      :let [c (nth (nth lines y) x)]]
                  {[x y] (str c)})
                (apply merge)) :size (count lines)}))

(defn neighbor? [[x1 y1] [x2 y2]]
  (or (and (= x1 x2) (= y1 (inc y2)))
      (and (= x1 (inc x2)) (= y1 y2))
      (and (= x1 (dec x2)) (= y1 y2))
      (and (= x1 x2) (= y1 (dec y2)))))

(defn group-elements
  "Groups elements of `coll` into a set of sets based on `membership-fn`.
   `membership-fn` should be a function of two arguments that returns true
   if those two elements belong in the same subset."
  [membership-fn coll]
  (reduce
   (fn [acc element]
      ;; Find all subsets whose members match `element` according to membership-fn
     (let [matching-subsets (filter
                             (fn [subset]
                               (some #(membership-fn element %) subset))
                             acc)]
       (cond
          ;; No matches: we must start a new subset
         (empty? matching-subsets)
         (conj acc #{element})

          ;; One match: add element to that subset
         (= 1 (count matching-subsets))
         (let [subset-to-extend (first matching-subsets)]
           (-> (disj acc subset-to-extend)
               (conj (conj subset-to-extend element))))

          ;; Multiple matches: merge them all into one, then add the new element
         :else
         (let [merged-subset (reduce clojure.set/union
                                     #{element}
                                     matching-subsets)]
           (-> (reduce disj acc matching-subsets)
               (conj merged-subset))))))
    ;; Start with an empty set of subsets
   #{}
   coll))

(defn perimeter [coll]
  (reduce
   (fn [acc [x y]]
     (+ acc (- 4 (count (filter #(neighbor? [x y] %) coll))))) 0
   coll))

(defn part1 [filename]
  (let [{:keys [grid _]} (parse-grid filename)
        groups (group-by val grid)]
    (->> groups
         (map (fn [[k v]] {k (map first v)}))
         (apply merge)
         (map #(map (fn [region] (* (count region) (perimeter region))) (group-elements neighbor? (val %))))
         (flatten)
         (reduce +))))

;; part2

(defn neighbors-along-x-axis? [[x1 y1] [x2 y2]]
  (and (= y1 y2) (or (= x1 (inc x2)) (= x1 (dec x2)))))

(defn neighbors-along-y-axis? [[x1 y1] [x2 y2]]
  (and (= x1 x2) (or (= y1 (inc y2)) (= y1 (dec y2)))))

(defn is-inside-corner? [coll [x y] [x1 y1] [x2 y2]]
  (if (neighbors-along-x-axis? [x y] [x1 y1])
    (let [check-x (+ x (- x1 x))
          check-y (+ y (- y2 y))]
      ;; (println check-x check-y)
      (not-any? #(= [check-x check-y] %) coll))
    (let [check-x (+ x (- x2 x))
          check-y (+ y (- y1 y))]
      ;; (println check-x check-y)
      (not-any? #(= [check-x check-y] %) coll))))

(defn count-corners [coll [x y]]
  (let [neighbors (filter #(neighbor? [x y] %) coll)]
    (cond
      (= 0 (count neighbors)) 4
      (= 1 (count neighbors)) 2
      (and (= 2 (count neighbors))
           (or (and (neighbors-along-x-axis? [x y] (first neighbors)) (neighbors-along-y-axis? [x y] (second neighbors)))
               (and (neighbors-along-y-axis? [x y] (first neighbors)) (neighbors-along-x-axis? [x y] (second neighbors)))))
      (if (is-inside-corner? coll [x y] (first neighbors) (second neighbors)) 2 1)
      (= 3 (count neighbors)) (let [tmp (map val (group-by #(neighbors-along-x-axis? [x y] %) neighbors))
                                    tmp2 (for [a (first tmp) b (second tmp)] (is-inside-corner? coll [x y] a b))]
                                (if (every? true? tmp2) 2 (if (some true? tmp2) 1 0)))
      (= 4 (count neighbors)) (let [tmp (map val (group-by #(neighbors-along-x-axis? [x y] %) neighbors))
                                    tmp2 (for [a (first tmp) b (second tmp)] (is-inside-corner? coll [x y] a b))]
                                (reduce + (map #(if % 1 0) tmp2)))
      :else 0)))

(defn sides [coll]
  (reduce
   (fn [acc [x y]]
     (+ acc (count-corners coll [x y]))) 0
   coll))

(defn part2 [filename]
  (let [{:keys [grid _]} (parse-grid filename)
        groups (group-by val grid)]
    (->> groups
         (map (fn [[k v]] {k (map first v)}))
         (apply merge)
         (map #(map (fn [region] (* (count region) (sides region))) (group-elements neighbor? (val %))))
         (flatten)
         (reduce +))))

;; Failed attempts at part 2 below
;;
;; (defn sides-helper
;;   "Returns the sides of a given coordinate. There are up to four sides, but
;;    some of them may be missing if they are neighbors of other coordinates."
;;   [[x y] coll]
;;   (let [all-possible [[[x y] [(inc x) y]] [[x (inc y)] [(inc x) (inc y)]] [[x y] [x (inc y)]] [[(inc x) y] [(inc x) (inc y)]]]
;;         minus-ones-with-neighbors (filter (fn [[p1 _]] (not (some #(= p1 %) coll))) all-possible)]
;;     minus-ones-with-neighbors))

;; (defn neighbor-to-the-left? [[x y] coll]
;;   (let [neighbor [(dec x) y]]
;;     (some #(= neighbor %) coll)))

;; (defn neighbor-to-the-right? [[x y] coll]
;;   (let [neighbor [(inc x) y]]
;;     (some #(= neighbor %) coll)))

;; (defn neighbor-to-the-top? [[x y] coll]
;;   (let [neighbor [x (dec y)]]
;;     (some #(= neighbor %) coll)))

;; (defn neighbor-to-the-bottom? [[x y] coll]
;;   (let [neighbor [x (inc y)]]
;;     (some #(= neighbor %) coll)))

;; (defn get-sides
;;   "Returns the sides of a given coordinate as a set. There are up to four sides, but
;;    some of them may be missing if they are neighbors of other coordinates."
;;   [[x y] coll]
;;   (let [sides (if (neighbor-to-the-top? [x y] coll) #{} #{[[x y] [(inc x) y]]})
;;         sides (if (neighbor-to-the-bottom? [x y] coll) sides (conj sides [[x (inc y)] [(inc x) (inc y)]]))
;;         sides (if (neighbor-to-the-left? [x y] coll) sides (conj sides [[x y] [x (inc y)]]))
;;         sides (if (neighbor-to-the-right? [x y] coll) sides (conj sides [[(inc x) y] [(inc x) (inc y)]]))]
;;     sides))

;; (defn can-combine? [[[x y] [x1 y1]] [[x2 y2] [x3 y3]]]
;;   (or
;;    (and (= x x1 x2 x3) (or (= y y2) (= y y3) (= y1 y2) (= y1 y3)))
;;    (and (= y y1 y2 y3) (or (= x x2) (= x x3) (= x1 x2) (= x1 x3)))))

;; (defn combine [[[x y] [x1 y1]] [[x2 y2] [x3 y3]]]
;;   (cond (and (= x x1 x2 x3) (= y y2)) [[x y1] [x y3]]
;;         (and (= x x1 x2 x3) (= y y3)) [[x y1] [x y2]]
;;         (and (= x x1 x2 x3) (= y1 y3)) [[x y] [x y2]]
;;         (and (= x x1 x2 x3) (= y1 y2)) [[x y] [x y3]]

;;         (and (= y y1 y2 y3) (= x x2)) [[x1 y] [x3 y]]
;;         (and (= y y1 y2 y3) (= x x3)) [[x1 y] [x2 y]]
;;         (and (= y y1 y2 y3) (= x1 x3)) [[x y] [x2 y]]
;;         (and (= y y1 y2 y3) (= x1 x2)) [[x y] [x3 y]]))

;; (defn combine-sides
;;   [membership-fn coll]
;;   (reduce
;;    (fn [acc element]
;;     ;;  (println acc)
;;       ;; Find all subsets whose members match `element` according to membership-fn
;;      (let [matching-subsets (filter
;;                              #(membership-fn element %)
;;                              acc)]
;;        (cond
;;           ;; No matches: we must start a new subset
;;          (empty? matching-subsets)
;;          (conj acc element)

;;           ;; One match: add element to that subset
;;          (= 1 (count matching-subsets))
;;          (let [subset-to-extend (first matching-subsets)]
;;            (-> (disj acc subset-to-extend)
;;                (conj (combine subset-to-extend element))))

;;           ;; Multiple matches: merge them all into one, then add the new element
;;          :else
;;          (let [merged-subset (reduce combine
;;                                      (first matching-subsets)
;;                                      matching-subsets)]
;;            (-> (reduce disj acc matching-subsets)
;;                (conj merged-subset))))))
;;     ;; Start with an empty set of subsets
;;    #{}
;;    coll))

;; (defn sides [coll]
;;   (combine-sides can-combine? (reduce (fn [acc [x y]]
;;                                         (apply conj acc (get-sides [x y] coll))) #{}
;;                                       coll)))