(ns day7.core
  (:gen-class)
  (:require [clojure.data.priority-map :as pm]
            [clojure.set :as set]
            [clojure.string :as str]))

(defn parse-input [input]
  (mapv (fn [line]
          (let [[_ node dep-node] (re-matches #"Step (\w) must be finished before step (\w) can begin." line)]
            [node dep-node]))
        (str/split-lines input)))

(defn available? [step completed edges]
  (not (some #(and (= step (second %)) (not (completed (first %)))) edges)))

(defn available-steps [edges completed]
  (let [nodes (set (map first edges))
        dep-nodes (set (map second edges))]
    (sort (filter #(and (available? % completed edges) (not (completed %))) (set/union dep-nodes nodes)))))

(defn weight [node]
  (+ 60 (- (int (first node)) 64)))

(defn part2 [edges num-of-workers]
  (let [start (take num-of-workers (available-steps edges #{}))
        work-queue' (reduce (fn [wq node]
                              (assoc wq node (weight node)))
                            (pm/priority-map)
                            start)
        workers' (- num-of-workers (count start))]
    (loop [work-queue work-queue'
           workers workers'
           time 0
           completed #{}]
      (println work-queue workers time completed)
      (if (and (empty? work-queue) (empty? (available-steps edges completed)))
        time
        (let [[node time'] (peek work-queue)
              completed' (conj completed node)
              workers' (inc workers)
              available (take workers' (filter #(not (work-queue %)) (available-steps edges completed')))
              work-queue' (into (pm/priority-map) (update-vals (dissoc work-queue node) (fn [v] (- v time'))))
              work-queue'' (reduce (fn [wq node]
                                     (assoc wq node (weight node)))
                                   work-queue'
                                   available)
              workers'' (- workers' (count available))]
          (recur work-queue'' workers'' (+ time time') completed'))))))

;; Graph Topological Sort
;;
;; The topo-sort function here performs a toplogical sort of the graph.  A
;; topological sort returns a list of nodes in dependency order. In our case, a
;; graph is represented as a collection of directed edges, each of which is a
;; two element vector with the "from node" and the "to node":
;;
;;     (topo-sort #{[:a :b] [:b :c] [:a :c]})
;;     => [:a :b :c]
;;
;; The nodes need not be keywords. They can be anything that can be compared
;; for equality.
;;
;; We use Kuhn's algorithm (source: https://en.wikipedia.org/wiki/Topological_sorting)
;;
;;     L ← Empty list that will contain the sorted elements
;;     S ← Set of all nodes with no incoming edge
;;     while S is non-empty do
;;         remove a node n from S
;;         add n to tail of L
;;         for each node m with an edge e from n to m do
;;             remove edge e from the graph
;;             if m has no other incoming edges then
;;                 insert m into S
;;     if graph has edges then
;;         return error (graph has at least one cycle)
;;     else
;;         return L (a topologically sorted order)

(defn incoming
  "Given a collection of edges, return all those that are incoming edges to node n."
  [edges n]
  (filter (fn [[x y]] (= y n)) edges))

(defn outgoing
  "Given a collection of edges, return all those that are outgoing edges from node n."
  [edges n]
  (filter (fn [[x y]] (= x n)) edges))

(defn remove-edges
  "Returns a vector of [edges s], where edges is the input edges collection
  with all outgoing edges from n removed, and s is a collection of nodes to add
  to our start nodes."
  [edges n]
  (loop [edges edges
         og (outgoing edges n)
         s []]
    (if (seq og)
      (let [edge (first og)
            [_ m] edge
            new-edges (remove #(= % edge) edges)] ; TODO make edges a set, use disj
        (if (= 0 (count (incoming new-edges m)))
          (recur new-edges (rest og) (conj s m))
          (recur new-edges (rest og) s)))
      [edges s])))

(defn start-nodes
  "Returns all nodes that have no incoming edges."
  [edges]
  (remove (set (map second edges)) (set (map first edges)))) ; TODO make edges a set, use disj

(defn topo-sort
  "Performs a topological sort of the graph implied by `edges`. `edges` is a
  collection of edges of the form `[x y]`, where x is the from-node and y is
  the to-node of the edge."
  [edges]
  (loop [edges edges
         s (start-nodes edges)
         L []]
    (println s)
    (if (seq s)
      (let [n (first s)
            [edges' s'] (remove-edges edges n)]
        (println s')
        (recur edges' (into (sorted-set) (into (rest s) s')) (conj L n)))
      (if (seq edges)
        [] ; FAIL, if we have edges left we don't have a DAG!
        L))))