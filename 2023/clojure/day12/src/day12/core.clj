(ns day12.core
  (:require [clojure.string :as str])
  (:gen-class))

;; inspired from https://old.reddit.com/r/adventofcode/comments/18ghux0/2023_day_12_no_idea_how_to_start_with_this_puzzle/
;;
;; analyze the string left to right.
;; if it starts with a ., discard the . and recursively check again.
;; if it starts with a ?, replace the ? with a . and recursively check again, AND replace it with a #and recursively check again.
;; it it starts with a #check if it is long enough for the first group, check if all characters in the first [grouplength] characters are not '.', and then remove the first [grouplength] chars and the first group number, recursively check again.
;; at some point you will get to the point of having an empty string and more groups to do - that is a zero. or you have an empty string with zero gropus to do - that is a one.

(def count-arrangements
  (memoize
   (fn [[r l]]
     (if (empty? r)
       (if (empty? l)
         1
         0)
       (let [first-char (first r)]
         (cond (= first-char \.) (count-arrangements [(subs r 1) l])
               (= first-char \?) (+ (count-arrangements [(str/replace-first r #"\?" "#") l]) (count-arrangements [(str/replace-first r #"\?" ".") l]))
               (= first-char \#) (if (empty? l)
                                   0
                                   (let [group-length (first l)
                                         rest-of-groups (rest l)]
                                     (if (>= (count r) group-length)
                                       (if (every? #(not= \. %) (take group-length r))
                                         (let [new-r (subs r group-length)]
                                           (if (empty? new-r)
                                             (count-arrangements [new-r rest-of-groups])
                                             (if (not= (first new-r) \#)
                                               (count-arrangements [(subs new-r 1) rest-of-groups])
                                               0)))
                                         0)
                                       0)))))))))

(defn parse-input [s]
  (->> s
       str/split-lines
       (map #(str/split % #" "))
       (map #(vector (first %) (flatten (map (fn [n] (Integer/parseInt n)) (str/split (second %) #",")))))))

(defn -main []
  (let [input (parse-input (slurp "resources/day12.txt"))
        copy5 (fn [s] (str (apply str (take 4 (repeat (str s "?")))) s))]
    (println "Part 1: " (reduce + (map count-arrangements input)))
    (println "Part 1: " (reduce + (map count-arrangements (map #(vector (copy5 (first %)) (flatten (repeat 5 (second %)))) input))))))

; *************************
; * Failed attempts below *
; *************************

;; (defn valid? [row v]
;;   (= v (into [] (map count (re-seq #"\#+" row)))))

;; (defn all-arrangements [row cnt]
;;   (if (= cnt 0)
;;     [(str/replace-first row #"\?" ".")]
;;     (let [num-of-placeholders (count (re-seq #"\?" row))]
;;       (if (= num-of-placeholders cnt)
;;         [(str (str/replace row #"\?" "#"))]
;;         (let [new-row (str/replace-first row #"\?" "#")]
;;           (into (all-arrangements new-row (dec cnt))
;;                 (all-arrangements (clojure.string/replace-first row #"\?" ".") cnt)))))))

;; (defn all-arrangements2 [row v cnt]
;;   (if (= cnt 0)
;;     (if (valid? (str/replace-first row #"\?" ".") v) 1 0)
;;     (let [num-of-placeholders (count (re-seq #"\?" row))]
;;       (if (= num-of-placeholders cnt)
;;         (if (valid? (str (str/replace row #"\?" "#")) v) 1 0)
;;         (let [new-row (str/replace-first row #"\?" "#")]
;;           (+ (all-arrangements2 new-row v (dec cnt))
;;              (all-arrangements2 (str/replace-first row #"\?" ".") v cnt)))))))

;; (def all-arrangements4
;;   "Version with memoization"
;;   (memoize
;;    (fn [r]
;;      (let [p1 (:p1 r)
;;            p2 (:p2 r)
;;            cnt (- (reduce + p2) (count (re-seq #"#" p1)))]
;;        (if (= cnt 0)
;;          (if (valid? (str/replace-first p1 #"\?" ".") p2) 1 0)
;;          (let [num-of-placeholders (count (re-seq #"\?" p1))]
;;            (if (= num-of-placeholders cnt)
;;              (if (valid? (str (str/replace p1 #"\?" "#")) p2) 1 0)
;;              (let [new-row (str/replace-first p1 #"\?" "#")]
;;                (+ (all-arrangements4 {:p1 new-row :p2 p2})
;;                   (all-arrangements4 {:p1 (str/replace-first p1 #"\?" ".") :p2 p2}))))))))))

;; ;; (defn -main []
;; ;;   (println "Part 1: " (reduce +
;; ;;                               (map (fn [l] (count (filter #(valid? % (:p2 l)) (all-arrangements (:p1 l) (- (reduce + (:p2 l)) (count (re-seq #"#" (:p1 l))))))))
;; ;;                                    (->> (slurp "resources/day12.txt")
;; ;;                                         str/split-lines
;; ;;                                         (map #(str/split % #" "))
;; ;;                                         (map #(assoc {} :p1 (first %) :p2 (into [] (map (fn [n] (Integer/parseInt n)) (str/split (second %) #","))))))))))

;; (defn copy2 [s] (str s "?" s))

;; ;; (defn parse-input [s]
;; ;;   (->> s
;; ;;        str/split-lines
;; ;;        (map #(str/split % #" "))
;; ;;        (map #(assoc {} :p1 (first %) :p2 (flatten (map (fn [n] (Integer/parseInt n)) (str/split (second %) #",")))))))

;; (defn solve [m]
;;   (let [c (- (reduce + (:p2 m)) (count (re-seq #"#" (:p1 m))))
;;         result (count (filter #(valid? % (:p2 m)) (all-arrangements (:p1 m) c)))]
;;     ;; (println result)
;;     result))

;; (defn solve2 [[idx m]]
;;   (let [c (- (reduce + (:p2 m)) (count (re-seq #"#" (:p1 m))))
;;         result (all-arrangements2 (:p1 m) (:p2 m) c)]
;;     (println idx)
;;     result))

;; (defn generate-combinations [num-set n]
;;   (combo/combinations num-set n))

;; (defn replace-chars [s idxs replacement]
;;   (apply str
;;          (map-indexed (fn [i ch]
;;                         (if (contains? (set idxs) i)
;;                           replacement
;;                           ch))
;;                       s)))

;; (defn replace-occurrences [s target-char occ-positions replacement]
;;   (let [occurrences (atom 0)]
;;     (apply str
;;            (map (fn [ch]
;;                   (if (= ch target-char)
;;                     (if (contains? (set occ-positions) (swap! occurrences inc))
;;                       replacement
;;                       ch)
;;                     ch))
;;                 s))))

;; (defn combinations [x y]
;;   (if (= x 1)
;;     (if (>= y 0) [[y]] [])
;;     (apply concat (for [i (range 0 (inc y))]
;;                     (map #(cons i %) (combinations (dec x) (- y i)))))))

;; (defn ordered-sets [x y]
;;   (filter #(= (count %) x)
;;           (filter #(apply <= %)
;;                   (combinations x y))))

;; (defn find-combinations [x y]
;;   (if (= x 1)
;;     (if (>= y 0) [[y]] [])
;;     (for [i (range 0 (inc y))
;;           rest (find-combinations (dec x) (- y i))]
;;       (cons i rest))))

;; (defn ranges
;;   [coll1 coll2 acc1 acc2 cnt]
;;   (if (empty? coll2)
;;     [(conj acc1 [cnt (+ cnt (first coll1))]) acc2]
;;     (let [operational (first coll1)
;;           damaged (first coll2)
;;           operational-range [cnt (+ cnt operational)]
;;           offset (second operational-range)
;;           damaged-range [offset (+ offset damaged)]]
;;       (recur (rest coll1) (rest coll2) (conj acc1 operational-range) (conj acc2 damaged-range) (+ cnt operational damaged)))))

;; (defn in-range? [x ranges]
;;   (some #(and (<= (first %) x (second %)) (<= (first %) (inc x) (second %))) ranges))

;; (defn overlap?
;;   [coll ranges]
;;   (every? #(in-range? % ranges) coll))

;; (defn all-arrangement3
;;   "An improved version - returns count of all valid arrangements"
;;   [{:keys [p1 p2]}]
;;   (let [count-of-damaged (reduce + p2)
;;         count-of-springs (count p1)
;;         all-combinations (filter #(every? pos-int? (rest (drop-last %))) (find-combinations (inc (count p2)) (- count-of-springs count-of-damaged)))
;;         all-ranges (map #(ranges % p2 [] [] 0) all-combinations)
;;         op (into [] (map first (filter #(= \. (second %)) (map-indexed vector p1))))
;;         damaged (into [] (map first (filter #(= \# (second %)) (map-indexed vector p1))))]
;;     (count (filter #(and (overlap? op (first %)) (overlap? damaged (second %))) all-ranges))))

;; (def all-arrangements-memo (memoize all-arrangement3))