(ns day25.core
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn parse-input [filepath]
  (->> (slurp filepath)
       clojure.string/split-lines
       (keep (fn [line]
               (when-let [[_ x y z w]
                          (re-matches #"(-?\d+),(-?\d+),(-?\d+),(-?\d+)" line)]
                 [(Long/parseLong x)
                  (Long/parseLong y)
                  (Long/parseLong z)
                  (Long/parseLong w)])))
       vec))

(defn manhattan-distance
  "Calculate the Manhattan distance between two points"
  [[x y z w] [x' y' z' w']]
  (+ (abs (- x x')) (abs (- y y')) (abs (- z z')) (abs (- w w'))))

(defn in-constellation? [constellation p]
  (some #(<= (manhattan-distance p %) 3) constellation))

(defn- add-to-component [connected component node]
  (-> connected
      (disj component)
      (conj (conj component node))))

(defn merge-constellations [superset p others]
  (-> (reduce disj superset others)
      (conj (-> (reduce set/union #{} others) (conj p)))))

(defn p1 [points-seq]
  (loop [points points-seq
         connected #{}]
    (if (empty? points)
      (count connected)
      (let [p (first points)
            tmp (filter #(in-constellation? % p) connected)]
        (recur (rest points)
               (cond
                 (= 1 (count tmp))
                 (add-to-component connected (first tmp) p)

                 (> (count tmp) 1)
                 (merge-constellations connected p tmp)

                 :else
                 (conj connected #{p})))))))

(defn -main []
  (println "Part 1: " (p1 (parse-input "resources/day25.txt"))))
