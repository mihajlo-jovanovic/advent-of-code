(ns day6.core)

(defn redistribute-blocks [coll]
  (let [value (apply max coll)
        index (first (keep-indexed (fn [idx item] (when (= item value) idx)) coll))
        n (count coll)
        q (quot value n)
        r (rem value n)]
    (map-indexed (fn [i x]
                   (let [q2 (if (<= (rem (+ n (- i index)) n) r) 1 0)
                         new-val (if (= i index) q (+ x q q2))]
                     new-val))
                 coll)))

(defn index-of-first-repeated
  "Returns the index of the first repeated element in the collection.
  Expects a collection of elements (e.g., a vector or list).
  Returns the zero-based index of the first element that appears more than once."
  [coll]
  (loop [seen #{}
         coll coll
         idx 0]
    (when-not (empty? coll)
      (let [current (first coll)]
        (if (contains? seen current)
          idx
          (recur (conj seen current) (rest coll) (inc idx)))))))