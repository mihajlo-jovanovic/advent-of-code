(ns day20.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-vec [s]
  (vec (map read-string (str/split (str/replace s #"[<>]" "") #","))))

(defn parse-item [s]
  (let [[k v] (str/split s #"=")]
    {(keyword k) (parse-vec v)}))

(defn parse-line [line]
  (apply merge (map parse-item (str/split line #", "))))

(defn parse-input [input]
  (map parse-line (str/split-lines input)))

(def tick
  (fn [particle]
    (let [v (:v particle)
          a (:a particle)]
      (assoc particle
             :v (map + v a)
             :p (map + (:p particle) (:v particle) (:a particle))))))

(def distance (fn [particle] (apply + (map #(Math/abs %) (:p particle)))))

(defn distinct-by [f coll]
  (let [groups (group-by f coll)]
    (map first (vals (filter (fn [[_ v]] (= (count v) 1)) groups)))))

(defn solve-p2 [input]
  (let [particles (parse-input (slurp input))]
    (loop [particles particles
           counter 0]
      (let [particles (map tick particles)
            particles (distinct-by :p particles)]
        (if (empty? particles)
          0
          (if (= counter 1000)
            (count particles)
            (recur particles (inc counter))))))))

(defn -main []
  (let [filename "resources/day20.txt"]
    (println "Part 2:" (solve-p2 filename))))