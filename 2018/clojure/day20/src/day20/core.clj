(ns day20.core
  (:require [clojure.string :as str]))

(defn move-to-room [c [x y]]
  (case c
    \N [x (dec y)]
    \S [x (inc y)]
    \E [(inc x) y]
    \W [(dec x) y]))

;;(def state {:loc [0 0] :path #{} :re (seq (chars (char-array "WNE")))})
;;(def state {:loc [0 0] :path #{} :re (seq (chars (char-array "ENWWW(NEEE|SSE(EE|N))")))})
(def state {:loc [0 0] :path {[0 0] 0}})

(defn move [c {:keys [loc path] :as state}]
  (let [new-c (move-to-room c loc)
        dist (get path loc)]
    {:loc new-c :path (if (path new-c) path (assoc path new-c (inc dist)))}))

(defn walk [{:keys [type a b] :as ast} state]
  (case type
    :lit (move (:ch ast) state)
    :emp state
    :alt {:loc (:loc state) :path (merge-with min (:path (walk a state)) (:path (walk b state)))}
    :cat (walk b (walk a state))))

(defn p1 [ast]
  (apply max (vals (:path (walk ast state)))))

(defn p2 [ast]
  (count (filter #(> % 999) (vals (:path (walk ast state))))))

;; ---------- Tokenizer ----------
(defn tokenize [^String s]
  (loop [i 0, out []]
    (if (>= i (.length s))
      out
      (let [ch (.charAt s i)]
        (cond
          (Character/isWhitespace ch) (recur (inc i) out)
          (= ch \() (recur (inc i) (conj out [:lparen ch]))
          (= ch \)) (recur (inc i) (conj out [:rparen ch]))
          (= ch \|) (recur (inc i) (conj out [:bar ch]))
          :else     (recur (inc i) (conj out [:lit ch])))))))

;; ---------- Recursive descent parser ----------
;; We'll parse with:
;; alt  := cat ('|' cat)*
;; cat  := atom atom*         (while next token can start an atom)
;; atom := LIT | '(' alt ')'

(declare parse-alt)

(defn starts-atom? [tok]
  (let [t (first tok)]
    (or (= t :lit) (= t :lparen))))

(defn parse-atom [toks]
  (let [[t v] (first toks)]
    (case t
      :lit
      [{:type :lit :ch v} (rest toks)]

      :lparen
      (let [[node toks'] (parse-alt (rest toks))
            [t2 _] (first toks')]
        (when (not= t2 :rparen)
          (throw (ex-info "Expected ')'" {:got (first toks')})))
        [node (rest toks')])

      :rparen
      [{:type :emp} toks]

      (throw (ex-info "Expected literal or '('" {:got (first toks)})))))

(defn cat-node [nodes]
  (reduce (fn [a b] {:type :cat :a a :b b})
          (first nodes)
          (rest nodes)))

(defn parse-cat [toks]
  (let [[a toks] (parse-atom toks)]
    (loop [nodes [a], toks toks]
      (if (starts-atom? (first toks))
        (let [[b toks'] (parse-atom toks)]
          (recur (conj nodes b) toks'))
        [(if (= 1 (count nodes)) (first nodes) (cat-node nodes))
         toks]))))

(defn parse-alt [toks]
  (let [[a toks] (parse-cat toks)]
    (loop [node a, toks toks]
      (if (= :bar (ffirst toks))
        (let [[b toks'] (parse-cat (rest toks))]
          (recur {:type :alt :a node :b b} toks'))
        [node toks]))))

(defn parse-regex [s]
  (let [toks (tokenize s)
        [ast rest-toks] (parse-alt toks)]
    (when (seq rest-toks)
      (throw (ex-info "Unexpected trailing tokens" {:rest rest-toks})))
    ast))
