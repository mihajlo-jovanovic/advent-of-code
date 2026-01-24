(ns day24.core
  (:require [clojure.string :as str]))

(def ^:private line-re
  ;; groups:
  ;; 1 units, 2 hp, 3 optional parens clause, 4 dmg, 5 attack type, 6 initiative
  (re-pattern
   (str "^\\s*(\\d+) units each with (\\d+) hit points"
        "(?: \\(([^)]*)\\))?"
        " with an attack that does (\\d+) ([a-z]+) damage at initiative (\\d+)\\s*$")))

(defn- parse-int ^long [s] (Long/parseLong s))

(defn- kwize [s]
  (-> s str/trim str/lower-case keyword))

(defn- parse-list [s]
  ;; "cold, slashing, fire" -> [:cold :slashing :fire]
  (->> (str/split s #",")
       (map kwize)
       (remove nil?)
       vec))

(defn- parse-weak-immune
  "Parses inside the parentheses.
   Examples:
     \"weak to fire\"
     \"immune to cold, slashing, fire\"
     \"immune to cold; weak to fire, slashing\""
  [clause]
  (let [empty-res {:weak-to [] :immune-to []}]
    (if (or (nil? clause) (str/blank? clause))
      empty-res
      (reduce
       (fn [acc part]
         (let [part (str/trim part)]
           (cond
             (re-find #"(?i)^weak to " part)
             (assoc acc :weak-to (parse-list (subs part (count "weak to "))))

             (re-find #"(?i)^immune to " part)
             (assoc acc :immune-to (parse-list (subs part (count "immune to "))))

             :else acc)))
       empty-res
       (str/split clause #";")))))

(defn parse-group-line
  "Parses one line into a map:
   {:units int
    :hit-points int
    :weak-to [...]
    :immune-to [...]
    :attack {:damage int :type kw}
    :initiative int}"
  [line]
  (when-let [[_ units hp clause dmg atk-type init] (re-matches line-re line)]
    (merge
     {:units      (parse-int units)
      :hit-points (parse-int hp)
      :attack     {:damage (parse-int dmg)
                   :type   (kwize atk-type)}
      :initiative (parse-int init)}
     (parse-weak-immune clause))))

(defn parse-group-lines [lines]
  (mapv parse-group-line lines))

(defn parse-input [f]
  (let [[immune infections] (str/split (slurp f) #"\n\n")]
    {:immune
     (mapv parse-group-line (drop 1  (str/split-lines immune)))
     :infections
     (mapv parse-group-line (drop 1  (str/split-lines infections)))}))

(defn calc-effective-power [{:keys [units] {:keys [damage]} :attack}]
  (* units damage))

(defn decorate
  "Modify shape of data: from a map of two lists to a single map of all groups indexed by id. need to also add :type"
  [{:keys [immune infections]}]
  (let [immune' (map #(assoc % :type :immune) immune)
        infections' (map #(assoc % :type :infect) infections)
        armies (into immune' infections')]
    (apply merge (map-indexed #(assoc {} %1 %2) armies))))

(defn calc-damage [{:keys [units] {:keys [type damage]} :attack :as attacker} {:keys [weak-to immune-to]}]
  (let [effective (calc-effective-power attacker)
        list-contains? (fn [coll el] (contains? (into #{} coll)  el))
        immune? (list-contains? immune-to type)
        weak? (list-contains? weak-to type)]
    (cond
      immune? 0
      weak? (* 2 effective)
      :else effective)))

(defn comp-groups-effective
  "a compare function by :effective of the struct 'group.' the sort order
   is superior to the higher effective power and if there's a tie, it is
   superior to the higher initiative."
  [grp1 grp2]
  (let [effective-power-grp1 (calc-effective-power grp1)
        effective-power-grp2 (calc-effective-power grp2)]
    (or (> effective-power-grp1 effective-power-grp2)
        (and (= effective-power-grp1 effective-power-grp2) (> (:initiative grp1) (:initiative grp2))))))

(defn comp-total-damage
  "a compare function by :total-damage of the struct 'group.' the sort order
   is superior to the higher total damage and if there's a tie, it is
   superior to the higher effective power, then initiative. used in target
   selection"
  [attacker grp1 grp2]
  (let [total-damage-grp1 (calc-damage attacker grp1)
        total-damage-grp2 (calc-damage attacker grp2)]
    (or (> total-damage-grp1 total-damage-grp2)
        (and (= total-damage-grp1 total-damage-grp2) (comp-groups-effective grp1 grp2)))))

(defn target-selection [armies]
  (let [select-target-id (fn [attack-grp targets]
                           (case (count targets)
                             0 nil
                             1 (key (first targets))
                             (key (first (sort-by val (partial comp-total-damage attack-grp) targets)))))]
    (reduce (fn [acc grp-id]
              (let [curr (get armies grp-id)
                    target-type (if (= :immune (:type curr)) :infect :immune)
                    selected-ids (into #{} (map :target-id (vals acc)))
                    remaining-targets (filter #(and (= target-type (:type (val %)))
                                                    (not (contains? selected-ids (key %)))
                                                    (pos? (calc-damage curr (val %)))) armies)
                    target-id (select-target-id curr remaining-targets)]
                (if target-id
                  (assoc acc grp-id (assoc curr :target-id target-id))
                  (assoc acc grp-id curr))))
            {}
            (map first (sort-by val comp-groups-effective armies)))))

(defn calc-units-lost [attacking defending]
  (let [double? (contains? (into #{} (:weak-to defending)) (:type (:attack attacking)))
        immune? (contains? (into #{} (:immune-to defending)) (:type (:attack attacking)))
        effective-power (* (:units attacking) (:damage (:attack attacking)))
        effective-power' (if double? (* 2 effective-power) effective-power)
        units (quot effective-power' (:hit-points defending))]
    (if immune? 0 units)))

(defn attack [armies]
  (let [attacking-only (filter #(:target-id (val %)) armies)
        attack-order-ids (map key (sort-by #(:initiative (val %)) #(compare %2 %1) attacking-only))]
    (reduce (fn [acc grp-id]
              (let [attacker (get acc grp-id)
                    defender (get acc (:target-id attacker))
                    units-lost (if attacker (calc-units-lost attacker defender) 0)    ;; attacker might've gotten killed first
                    all-units-killed? (if defender (>= units-lost (:units defender)) false)]
                (if all-units-killed?
                  (dissoc acc (:target-id attacker))
                  (if defender
                    (assoc acc (:target-id attacker) (update defender :units - units-lost))
                    acc))))
            armies attack-order-ids)))

(defn round [armies]
  (let [after-target-selection (target-selection armies)
        after-attack (attack after-target-selection)
        reset-targets (into {} (map #(assoc {} (key %) (dissoc (val %) :target-id)) after-attack))]
    reset-targets))

(defn fight [armies]
  (loop [a armies]
    (let [prev-units (apply + (map :units (vals a)))
          a' (round a)
          curr-units (apply + (map :units (vals a')))
          types (distinct (map :type (vals a')))]
      (cond
        (= 1 (count types)) a'
        (= prev-units curr-units) {0 {:type :infect}} ;; (do (println "STALEMATE detected! Units:" curr-units "Types:" types "Groups:" a') a')
        :else (recur a')))))

(defn p1 [input]
  (let [final-state (fight (decorate input))]
    (apply + (map :units (vals final-state)))))

(defn boost [{:keys [immune infections]} n]
  {:immune
   (map #(update-in % [:attack :damage] + n) immune)
   :infections infections})

(defn winner-type [input]
  (let [final-state (fight (decorate input))]
    (first (map :type (vals final-state)))))

(defn p2 [input]
  (let [smallest-boost (inc (last (take-while #(= :infect (winner-type (boost input %))) (range 1 2000))))
        final-state (fight (decorate (boost input smallest-boost)))]
    (apply + (map :units (vals final-state)))))
