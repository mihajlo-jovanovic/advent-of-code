(ns day9.core
  (:gen-class))

(defn list-insert [lst elem index]
  (let [[l r] (split-at index lst)]
    (concat l [elem] r)))

;; (defn list-insert2 [lst elem index]
;;   (let [[left _ right] (ft-split-at lst index)]
;;     (ft-concat (conj left elem) right)))

(defn step
  "Advances the game state by one step. This function updates the state of the game by either placing a new marble
  or handling the special case where the marble number is a multiple of 23.

  Parameters:
  - state: A map representing the current state of the game. It contains the following keys:
    - :current-marble: The index of the current marble.
    - :marbles: A list of integers representing the marbles in the circle.
    - :last-marble: The number of the last marble placed.
    - :players: A vector of integers representing the scores of the players.
    - :current-player: The index of the current player.

  Returns:
  - A new state map with updated values for :current-marble, :marbles, :last-marble, :players, and :current-player."
  [{:keys [current-marble marbles last-marble players current-player] :as state}]
  (let [new-last-marble (inc last-marble)
        new-current-player (mod (inc current-player) (count players))]
    (if (zero? (mod new-last-marble 23))
      (let [player current-player
            new-score (+ new-last-marble (nth players player))
            index-of-marble-to-remove (mod (- current-marble 7) (count marbles))
            new-score (+ new-score (nth marbles index-of-marble-to-remove))
            new-marbles (concat (take index-of-marble-to-remove marbles)
                                (drop (inc index-of-marble-to-remove) marbles))
            new-current-marble index-of-marble-to-remove]
        {:players (assoc players player new-score)
         :marbles new-marbles
         :current-marble new-current-marble
         :current-player new-current-player
         :last-marble new-last-marble})
      (let [new-current-marble (+ 2 current-marble)
            new-current-marble' (if (> new-current-marble (count marbles))
                                  (- new-current-marble (count marbles))
                                  new-current-marble)]
        (assoc state :current-player new-current-player
               :current-marble new-current-marble'
               :marbles (list-insert marbles new-last-marble new-current-marble')

               :last-marble new-last-marble)))))

(defn part1 [num-of-players last-marble]
  (let [state {:players (vec (repeat num-of-players 0))
               :marbles [0 2 1]
               :current-player 2
               :current-marble 1
               :last-marble 2}]
    (apply max (:players (last (take (dec last-marble) (iterate step state)))))))

;; (def players (atom {}))

;; (defn part1-alt [num-of-players last-marble-worth]
;;   (do (reset! players (vec (repeat num-of-players 0)))
;;       (loop [marbles [0 2 1]
;;              player 2
;;              current 1
;;              last-marble 2]
;;         (if (>= last-marble last-marble-worth)
;;           (apply max @players)
;;           (let [new-last-marble (inc last-marble)
;;                 new-current-player (mod (inc player) num-of-players)]
;;             (if (zero? (mod new-last-marble 23))
;;               (let [new-score (+ new-last-marble (nth @players player))
;;                     index-of-marble-to-remove (mod (- current 7) (count marbles))
;;                     new-score (+ new-score (nth marbles index-of-marble-to-remove))
;;                     new-marbles (concat (take index-of-marble-to-remove marbles)
;;                                         (drop (inc index-of-marble-to-remove) marbles))
;;                     new-current-marble index-of-marble-to-remove]
;;                 (swap! players assoc player new-score)
;;                 (recur new-marbles new-current-player new-current-marble new-last-marble))
;;               (let [new-current-marble (+ 2 current)
;;                     new-current-marble' (if (> new-current-marble (count marbles))
;;                                           (- new-current-marble (count marbles))
;;                                           new-current-marble)]
;;                 (recur (list-insert marbles new-last-marble new-current-marble') new-current-player new-current-marble' new-last-marble))))))))

(defn -main []
  (time (part1 466 71436))
  ;; (println (part1-alt 9 25))
  )