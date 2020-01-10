(ns day6-clj.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn add-nodes
  [t]
  (letfn [(helper [t l cnt]
            (if (next t)
              (if (= 3 (count t))
                (let [lft (first (rest t))
                      rgt (second (rest t))]
                  (+ (helper lft (inc l) (+ l cnt))
                     (helper rgt (inc l) (+ l cnt))
                     cnt))
                (helper (first  (rest t)) (inc l) (+ l cnt)))
              l))]
    (helper t 1 0)))

(defn bf
  "breadth-first tree traversal"
  [l & roots]
  (if (seq? roots)
    (concat (list (* l (count  (map first roots))))
            (apply bf (inc l) (mapcat rest roots)))))

(defn test1
  [& roots]
  (mapcat rest roots))

(defn day6
  [tree]
  (if (next tree)
    (+ (day6 (second tree)) (day6 (last tree)) 1)
    1))

(defn ->branch
  [id children]
  (cons id children))

(defn ->leaf
  [id]
  id)

(defn descendants
  [adj-list root]
  (map second (filter #(= (first %) root) adj-list)))

(defn ->tree
  [adj-list root]
  (let [->tree' (partial ->tree adj-list)]
    (if-let [children-ids (descendants adj-list root)]
      (->branch root (map ->tree' children-ids))
      (->leaf root))))
