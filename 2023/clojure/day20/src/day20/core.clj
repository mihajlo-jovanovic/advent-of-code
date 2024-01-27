(ns day20.core
  (:require [clojure.string :as string]))

(ns-unmap *ns* 'receive)  ;; required after changing arity of receive

(defmulti receive :t)

(defmethod receive :broadcaster [{:keys [:d]} [_ pulse]]
  {:out (map #(vector % pulse) d) :state nil})

(defmethod receive :flip-flop [{:keys [:d :state]} [_ pulse]]
  (cond
    (and (not pulse) (not state)) {:out (map #(vector % true) d) :state true}
    (and (not pulse) state) {:out (map #(vector % false) d) :state false}
    :else nil))

(defmethod receive :conjunction [{:keys [:d :state]} [from pulse]]
  (let [new-mem (assoc state from pulse)
        all-on? (reduce #(and %1 %2) true (vals new-mem))]
    (if all-on?
      {:out (map #(vector % false) d) :state new-mem}
      {:out (map #(vector % true) d) :state new-mem})))

(defmethod receive :default [_ _] nil)

(defn parse-input [input]
  (let [parse-line (fn [l] (let [parts (string/split l #" -> ")
                                 k (first parts)
                                 d (->> (string/split (second parts) #", ") (map string/trim) (map keyword))
                                 t (case (.charAt k 0) \% :flip-flop \& :conjunction :broadcaster)]
                             {(keyword (if (not= k "broadcaster") (subs k 1) k)) {:t t :d (into [] d) :state (if (= t :flip-flop) false {})}}))
        init-conj-state (fn [module-config] (loop [conf module-config
                                                   conjunction-modules (keys (filter (fn [[_ v]] (= :conjunction (:t v))) conf))]
                                              (if (empty? conjunction-modules)
                                                conf
                                                (let [mod (first conjunction-modules)
                                                      input-modules (keys (filter (fn [[_ v]] (contains? (set (:d v)) mod)) conf))
                                                      init-state (reduce #(assoc %1 %2 false) {} input-modules)
                                                      mod-new (assoc (get conf mod) :state init-state)]
                                                  (recur (assoc conf mod mod-new) (rest conjunction-modules))))))]
    (->> (string/split-lines input) (map parse-line) (apply merge) init-conj-state)))

(defn button-press [module-config {:keys [:state :cnt-low :cnt-high]}]
  (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [:start :broadcaster false])
         state state
         cnt-low cnt-low
         cnt-high cnt-high]
    (if (empty? queue)
      {:state state :cnt-low cnt-low :cnt-high cnt-high}
      (let [[from to pulse] (peek queue)
            module (get module-config to)
            update-module-state (if (get state to) (assoc module :state (get state to)) module)
            res (receive update-module-state [from pulse])
            state-new (if res (assoc state to (:state res)) state)]
        ;; (println "current" [from to pulse])
        ;; (println "update-module-state" update-module-state)
        ;; (println "res" res)
        ;; (println "state-new" state-new)
        ;; (println)
        (recur (apply conj (pop queue) (map #(cons to %) (:out res))) state-new (if pulse cnt-low (inc cnt-low)) (if pulse (inc cnt-high) cnt-high))))))

(defn part1 [input]
  (let [parsed (parse-input input)
        res (last (take 1001 (iterate (partial button-press parsed) {:state {} :cnt-low 0 :cnt-high 0})))]
    (* (:cnt-low res) (:cnt-high res))))

(defn button-press-p2 [module-config frm {:keys [:state :cnt]}]
  (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [:start :broadcaster false])
         state state
         found false
         cnt cnt]
    (if (empty? queue)
      {:state state :found found :cnt (inc cnt)}
      (let [[from to pulse] (peek queue)
            found-new (if (not found) (and (= to :ns) (= from frm) pulse) true)
            module (get module-config to)
            update-module-state (if (get state to) (assoc module :state (get state to)) module)
            res (receive update-module-state [from pulse])
            state-new (if res (assoc state to (:state res)) state)]
        (recur (apply conj (pop queue) (map #(cons to %) (:out res))) state-new found-new cnt)))))

(defn part2 [input]
  (let [parsed (parse-input input)
        modules (keys (:state (:ns parsed)))]
    (reduce * (map (fn [m] (inc (:cnt (last (take-while #(not (:found %)) (iterate (partial button-press-p2 parsed m) {:state {} :cnt 0})))))) modules))))