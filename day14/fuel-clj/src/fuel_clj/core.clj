(ns fuel-clj.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn ingredients
  [l m raw res]
  (if (empty? l)
    res
    (if (contains? raw (first l))
      (ingredients (rest l) m raw (conj res (first l)))
      (ingredients (clojure.set/union res (get m (first l))) m raw res))))

