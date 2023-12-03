(ns day4.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [clojure.set :as cset]))


(defn parse-string [input-string]
  (let [parts (str/split input-string #"\|")]
    (mapv (fn [part]
            (mapv #(Integer. %) (-> (str/trim part)
                                    (str/split #"\s+"))))
          parts)))

(defn parse-input
  [f]
  (->> f
       slurp
       (str/split-lines)
       (map parse-string)))

(defn score [card]
  (let [winning-numbers (set (first card))
        elf-numbers (set (second card))]
    (count (cset/intersection winning-numbers elf-numbers))))

(defn p1 [cards]
  (reduce + (map #(int (math/pow 2 (dec (score %)))) cards)))

(defn combine [coll pair]
  (let [num-of-copies (get coll (first pair))
        l (take (second pair) (range (inc (first pair)) (count coll)))
        copies (mapv #(vector % num-of-copies) l)]
    (reduce (fn [m pair]
              (let [n (get coll (first pair))]
                (assoc m (first pair) (+ n (second pair)))))
            coll
            copies)))

(defn p2 [cards]
  (let [num-of-cards (count cards)]
    (reduce +
            (reduce combine
                    (vec (take num-of-cards (repeat 1)))
                    (map-indexed vector (map score cards))))))

(defn -main
  [& _]
  (let [cards (parse-input "resources/input.txt")]
    (println "Part 1: " (p1 cards))
    (println "Part 2: " (p2 cards))))