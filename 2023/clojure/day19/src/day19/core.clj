(ns day19.core
  (:require [clojure.string :as str])
  (:gen-class))

(defn drop-last-char [s]
  (subs s 0 (dec (count s))))

(defn parse-line [line]
  (let [parts (str/split line #"\{")
        workflow (first parts)
        exp (drop-last-char (last parts))]
    (assoc {} (keyword workflow) exp)))

(defn parse-input [s]
  (->> s
       str/split-lines
       (into {} (map parse-line))))


(defn parse-str-to-map [s]
  (->> (str/split (subs s 1 (dec (count s))) #",")
       (map (fn [s] (str/split s #"="))) ; Split each pair on '='
       (map (fn [[k v]] [(keyword k) (Integer/parseInt v)])) ; Convert to keyword and integer
       (into {})))


(defn parse-ratings [s]
  (->> s
       str/split-lines
       (map parse-str-to-map)))

(defn accepted?
  "Returns true if the part is accepted by the workflow"
  [rule workflows part]
  (cond
    (= "A" rule) true
    (= "R" rule) false
    (re-matches #"[a-z]{2,3}" rule) (accepted? (get workflows (keyword rule)) workflows part)
    :else (let [f (subs rule 0 (.indexOf rule ","))
                l (subs rule (inc (.indexOf rule ",")))
                parts (re-seq #"[^:<>]+|." f)
                key (first parts)
                operator (second parts)
                value (nth parts 2)
                outcomes (str/split (nth parts 4) #",")]
            (if (= operator "<")
              (if (< (get part (keyword key)) (Integer/parseInt value))
                (accepted? (first outcomes) workflows part)
                (accepted? l workflows part))
              (if (> (get part (keyword key)) (Integer/parseInt value))
                (accepted? (first outcomes) workflows part)
                (accepted? l workflows part))))))

(defn merge-constraint
  "Helper function to merge a constraint into the map of constraints"
  [m k v op]
  (let [val1 (get m k)]
    (if (nil? val1)
      (if (= op "<")
        (assoc m k [1 (dec v)])
        (assoc m k [(inc v) 4000]))
      (if (and (= op "<") (< v (second val1)))
        (assoc m k [(first val1) (dec v)])
        (if (and (= op ">") (> v (first val1)))
          (assoc m k [(inc v) (second val1)])
          m)))))

(defn constraints
  "Returns a map of constraints for the given workflows"
  [rule workflows constraint acc]
  (cond
    (= "A" rule) (conj acc constraint)
    (= "R" rule) '()
    (re-matches #"[a-z]{2,3}" rule) (constraints (get workflows (keyword rule)) workflows constraint acc)
    :else (let [f (subs rule 0 (.indexOf rule ","))
                l (subs rule (inc (.indexOf rule ",")))
                parts (re-seq #"[^:<>]+|." f)
                key (first parts)
                operator (second parts)
                value (Integer/parseInt (nth parts 2))
                outcomes (str/split (nth parts 4) #",")]
            (concat (constraints (first outcomes) workflows (merge-constraint constraint (keyword key) value operator) acc)
                    (constraints l workflows (merge-constraint constraint (keyword key) (if (= "<" operator) (dec value) (inc value)) (if (= "<" operator) ">" "<")) acc)))))

(defn p1
  [workflows ratings]
  (reduce + (flatten (map vals (filter (partial accepted? "in" workflows) ratings)))))

(defn calculate-combinations [constraints total-range]
  (let [var-keys [:s :a :m :x]
        calculate-for-constraint (fn [constraint]
                                   (reduce
                                    (fn [acc var-key]
                                      (* acc (if-let [range (constraint var-key)]
                                               (+ 1 (- (second range) (first range)))
                                               total-range)))
                                    1
                                    var-keys))]
    (reduce + (map calculate-for-constraint constraints))))

(defn p2 [workflows]
  (let [constraints (constraints "in" workflows {} [])
        total-range 4000]
    (calculate-combinations constraints total-range)))

(defn -main
  []
  (let [input (slurp "resources/day19.txt")
        parts (str/split input #"\n\n")
        workflows (parse-input (first parts))
        ratings (parse-ratings (second parts))]
    (println "Part 1: " (p1 workflows ratings))
    (println "Part 1: " (p2 workflows))))