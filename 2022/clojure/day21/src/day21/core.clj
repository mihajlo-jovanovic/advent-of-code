(ns day21.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-monkey [s]
  "Each line contains the name of a monkey, a colon, and then the job of that monkey"
  (let [tokens (str/split s #": ")
        k (first tokens)
        v (second tokens)]
    {k (if-let [op (parse-long v)]
         op
         (let [exp (str/split v #" ")]
           (cons (symbol (second exp)) (cons (first exp) (list (last exp))))))}))

(defn evaluate [coll acc]
  (if (and (seq? acc) (some string? acc))
    (map #(if (string? %) (evaluate coll (coll %)) %) acc)
    acc))

(defn -main
  [& _]
  (let [m (into (hash-map) (map parse-monkey (-> "day21.txt"
                                               io/resource
                                               slurp
                                               str/split-lines)))
        exp (evaluate m (m "root"))]
    (println "Part 1 solution: " (eval exp))))

;; for part 2, got lazy so just did binary search manually...only one side of exp is affected so makes it easy