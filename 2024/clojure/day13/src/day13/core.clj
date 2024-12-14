(ns day13.core
  (:gen-class)
  (:require [clojure.string :as s]))

(defn parse-claw-machine-config [lines]
  (letfn [(parse-coordinates [line]
            (let [parts (s/split line #", ")]
              [(Long/parseLong (re-find #"\d+" (first parts)))
               (Long/parseLong (re-find #"\d+" (second parts)))]))]
    {:button-a (parse-coordinates (first lines)) :button-b (parse-coordinates (second lines)) :prize (parse-coordinates (last lines))}))

(defn parse-input [filename]
  (->> (s/split (slurp filename) #"\n\n")
       (map s/split-lines)
       (map parse-claw-machine-config)))

(defn cart [colls]
  (if (empty? colls)
    '(())
    (for [more (cart (rest colls))
          x (first colls)]
      (cons x more))))

(defn find-winning-combination [{:keys [button-a button-b prize]}]
  (let [a-seq (map-indexed vector (map #(vector (* (first button-a) %) (* (second button-a) %)) (range 101)))
        b-seq (map-indexed vector (map #(vector (* (first button-b) %) (* (second button-b) %)) (range 101)))]
    (filter (fn [[[_ [x1 y1]] [_ [x2 y2]]]] (and (= (second prize) (+ y1 y2)) (= (first prize) (+ x1 x2)))) (cart [a-seq b-seq]))))

(defn find-winning-combination-alt [{:keys [button-a button-b prize]}]
  (let [determinant (- (* (first button-a) (second button-b)) (* (second button-a) (first button-b)))
        determinant-a (- (* (first prize) (second button-b)) (* (second prize) (first button-b)))
        determinant-b (- (* (second prize) (first button-a)) (* (first prize) (second button-a)))]
    [(/ determinant-a determinant) (/ determinant-b determinant)]))

(defn -main []
  (let [input (parse-input "resources/input.txt")
        ans-p1 (mapv first (filter #(= (count %) 1) (map find-winning-combination input)))
        tmp (map #(mapv first %) ans-p1)
        input-p2 (map #(update-in % [:prize] (fn [x] (map (partial + 10000000000000) x))) input)
        ans (filter #(every? int? %) (map find-winning-combination-alt input-p2))]
    (println "Part 1: " (reduce (fn [acc [a b]] (+ acc (* 3 a) b)) 0 tmp))
    (println "Part 2: " (reduce (fn [acc [a b]] (+ acc (* 3 a) b)) 0 ans))))