(ns day7.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-node [s]
  (let [[name-and-weight children] (str/split s #" -> ")
        [name weight-str] (str/split name-and-weight #" ")
        weight (Integer/parseInt (re-find #"\d+" weight-str))
        children-list (if children (str/split children #", ") [])]
    {name {:weight weight :children children-list}}))

(defn parse-inputs [s]
  (apply merge (map parse-node (str/split s #"\n"))))

(defn find-root [nodes]
  (let [all-nodes (keys nodes)
        children (mapcat #(get-in % [:children]) (vals nodes))]
    (first (set/difference (set all-nodes) (set children)))))

(defn children [tree node]
  (:children (get tree node)))

(defn tree-sequence [tree root]
  (tree-seq (constantly true) (partial children tree) root))

(defn sum-all-weights [tree root]
  (reduce + (map #(:weight (get tree %)) (tree-sequence tree root))))

(defn find-unique [nums]
  (let [grouped (group-by identity nums)]
    (->> grouped
         (filter #(= 1 (count (second %))))
         ffirst)))

(defn solve-p2 [tree root expected-weight]
  (let [child-nodes (children tree root)
        child-weights (map (partial sum-all-weights tree) child-nodes)
        unique-weight (find-unique child-weights)]
    (if-not unique-weight
      expected-weight
      (let [unique-child (nth child-nodes (.indexOf child-weights unique-weight))
            weight-diff (- (first child-weights) (first (remove #(= % unique-weight) child-weights)))
            child-weight (:weight (get tree unique-child))]
        (solve-p2 tree unique-child (- child-weight weight-diff))))))

(defn -main []
  (let [input (slurp "resources/day7.txt")
        nodes (parse-inputs input)
        root (find-root nodes)]
    (println "Part 1:" root)
    (time (println "Part 2:" (solve-p2 nodes root 0)))))