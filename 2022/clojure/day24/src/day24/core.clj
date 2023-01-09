(ns day24.core
  (:require
    [clojure.set :as set]
    [clojure.string :as str])
  (:gen-class))

(defn parse-line [[x s]]
  (let [l (->> s
               drop-last
               (drop 1)
               (map-indexed vector)
               (filter #(not= (second %) \.)))
        xform (map (fn [n] {:y (first n) :d (last n)}))
        xf (into [] xform l)]
    (map #(assoc % :x x) xf)))

(defn parse-grid [s]
  (->> s
       str/split-lines
       drop-last
       (drop 1)
       (map-indexed vector)
       (map parse-line)
       (filter not-empty)
       flatten))

;(def xform (map (fn [n] {:y (first n) :d (last n)})))

(defn move [b max-x max-y]
  (case (:d b)
    \> (update b :y #(mod (inc %) max-y))
    \< (update b :y #(mod (dec %) max-y))
    \v (update b :x #(mod (inc %) max-x))
    \^ (update b :x #(mod (dec %) max-x))))

(def init-state {:e {:x -1 :y 0} :grid (parse-grid "#.########################################################################################################################\n#<<<^<^^>v<<^<^><><v<^<<.v^><<<v>><^v<vv>v<vv>^>>^^^^.>>^vv<^<<>^.vv.<>>v>v^>^<><.vv^v.^<^v>^vv<^vv<<>v<<>><<<v^^>v><<<^>#\n#>^^v><.<>>v.v.^>v^..>^<vv>v^><v^<<>v>>.^v^vv^^>.^><^>>^>vv>>v^>vvvv^v^vv<<^<^>^>^v<v^>><><v^^^<v.^v<>^.>v><vv.^<^^^^<>>>#\n#<<.><>v<>.v^<^^v<^^>.^.>>v.^^^v>^^^.<><.^.<>>^<>.v><^^^>^>^^^^><vv^.v^<<^v>v^^^<<<^<..^vv><.^v<vv^>>vv>v.><v^>>^<v^>>vv>#\n#.><^v.>^v>>vv><^>.>>>>>^.>>.>^v>v^>v^^>v^^..>^.^<^.v<>v<<<^^^.vvv>>.<>^vvv>>.^<^v^>^<^<vv>^>^.<>^<<>v.<<<.^<>^>.<v.vvv^>#\n#>><v<>.v><<^^vvv<<<^>^v^v><^>^<>>v.<.<>^<>vv>vv>^.v><<<^<v^vvv.<<<><^<^<^.^^vv>^v^^v<>>vv^<^.^v^v<v^>^.>.v>v^^<v<^<>^>v>#\n#>>><v<.>>v^<.^vvvv<v<^<<v>>v>vv.^><v^.>>vv><<.<^.^v>^^><>^<^v<vvvvv<<<<^^>..>>>v^^>.vv<>.^^^.<v>v^>>^>^^>v^v^^<.>>^^<.<>#\n#<<v>>v<>^><^v<>^>vvv..v.^.<^.v.vv^.<.v<>v>^<^v^<^>><.<>v^v>^<<^<v^^<v^<v^.<v><<<>^^<><>.v><^^^<<^<v>><vv<^<^.^<<^.^<<v>>#\n#>>>^^>v^>^>>v><<>^<v><>>^>.<v<<>v<>^<>^v.<<^.^^^^>^.v>v><v^>vv^^^.>v^<v^^v<<^.vv^>vvv>^>>v<.^^vv<<v.v..^v.^vvv.<.>^<^v>>#\n#><^vv^>>v.<^<v^^><v^>^><<<v^>v><v^^>><vv^><.>>^vv>><vv<^v^v^>^>>.vvvvvv<>>>.>^<vv^<vv<<<^^v^>.v><^.<<^<>^^>.^v><^^vv.^>>#\n#>..<<v<<>>.<^v<<.>v^<..^<^^v^>.><.<v>>v><<>^>>v<vvv>>v<v>.vvvv<.<<<^<^v>^<v^>>v.^v^<^v..^..<^<><>^<<>^>^<<^^.>^^<v<>v<><#\n#<v^v>v<^^<v>>vv^v<..^>v>>>.>^^<>>>^..>>>^<<^<^<>^<v>>^>>><^v^v^<.^<<<^v^.^^^<v>>><^^<>>><..vv.^.^<<v^v.>>^v^^><>><.v^>v>#\n#.<^^<v>v.<v<^.^v^<.v^vvv^vv^<..v<.>>>v<<>>vv>>^.>^.v>vv>.v^<<v^>>v.<^^<^<<>^^.^^>^>>^<^<<^^vv^^.<.><^.>^^<>v<.^v<>v><v<.#\n#>^.v^^><^<.>^v>v><>>>..>.v>vvv<<vv^.>v>.>v.<^vv.^>^vvv>.>v^v>>.>^^<<>>.^.>>>^>v<>v>v^^<>^<^.^><.vv^^<<.^v>>.>>>.^>^^^^^<#\n#..^>vv><^^.>>^^.<<<v..<<>^.<.<^^.v<<<^<<>><>v>v<^^^.<><^<<>v^^<^v^>^v>v<<^<<v<><<>.><..<^vv^<.><>^^><<.>>^.^v><>v^<^v>^>#\n#><<>v>.>^<<v>>>>>^>v..<vv<^<v>>^<<^v<v<<<<.v^>v.v<^>>>v.^><^><>.<>.^v><><<v>^^^^.<<.>^>><>.<<.<^<.^>..^>^.^>^v^v.<.^v>><#\n#><><^v.<<<<>>v<<<<^v<<^^<><<.>^^.^.^.<>vv<<<v>^<><^^<^>^>^^.v<^>>vv^v.>^.<<><vv>>.>.<><^<.<<>.v>.^>>>^<<>.<v>^^v^v^<>.<<#\n#<^>^v^>v^v.<^^>.^>^<<>.<.<<vvv>v>v^v^vvv>v^v^v<^^^<^>>v.^v.^>v><.^^^<>^>^^vv.v>>>^v<<>>^<^.v^>>>><v>v><^<.^v^v^^.<<>><^<#\n#>^>v.><v<^<><>><v<^^<^><<<<^v>^<v^>v<<>^>>.><v<v<^^>^<><><^<.<.<><v^^.^>>v^v^.>><vvvv^v^vv><<^<<^>.v>>>vv>v^v^^<v^^>^>><#\n#<<v^^>vv^^v.<<v<v<^><^v.>.<^v^>^^>^<^^v>>v<^^vv<>^v^.^v<>.<^^>^^v><>^^v.><.v^^>.<<.v^<v<^<>^<^><v^>.>^v>v<v<.>v<.<v.>.<<#\n#>^<<v^vv..vv^v><>>>^>v.>v^..^>^vv>^^>v^v<>.><v<<<<^><>v^.v^>.>^vv>vv>.>^<<.v<^^v^<<>^^^>^.<.v>^><.<^<^>>><.<<^^^>^<^<v<>#\n#<^><v<v><<<>v>>vv.>^<v>><>v>^.v^<v><<^<>>^<^^.<v<<vv>^v.v.<vv<^<<^>v>v.<<<v^><^^>.>>v>.<^vv.^v>^^^<><<^<>>><v^v>>>v<^>v>#\n#>>>^.v><<<>^v<.<.^><v>><>.<.<>v^v>>>^<^^vvv.vv<v<^^^>><^v>>>v>v<>v<v<vv^.^>v<>^>>>^<<v<vv^>><.v<^v<v^<.<>v>^>^><.>v<>.><#\n#<^<v>v.vv>vv^v<.>>.v.^..>^v<.v><>^<.^^<v><^.v^<v^vv^^v^<>>^<^><v.vvv^>.^v>><<>><<<>.<v^<^.<vv^.v<><v^v^>^v^>v<<v^>vv>^v>#\n#<><.v.^v>>><^.<.^<<v>^^><<^>>.^^.v>vv^^.v>^<<^^<<<^>>^>^>v..^v><>v<vv<<v^v<>>><v>^v<><.>v>v>v^^.^>>vv^v.vv<>v^<><^^><vv<#\n#<<v<v>^><^v^<^<v>><.v<.>^vv^>^<v^v>v<.vvv<v<^^>^^^^v^<<v^>v<v^vv>.^vv>v^.>>.^<.>>v<..^v.>^<<v<vvv^<..^<<>^^>v<<><^^><>>>#\n########################################################################################################################.#")})

;(def init-state {:e {:x -1 :y 0} :grid (parse-grid "#.######\n#>>.<^<#\n#.<..<<#\n#>v.><>#\n#<^v^^>#\n######.#")})

(defn move-candidates [loc max-x max-y]
  (->> [(update loc :x inc) (update loc :x dec) (update loc :y inc) (update loc :y dec)]
       (filter #(and (>= (:x %) 0) (>= (:y %) 0) (< (:x %) max-x) (< (:y %) max-y)))))

(defn valid? [loc grid]
  (not-any? #(and (= (:x loc) (:x %)) (= (:y loc) (:y %))) grid))

(defn next-states [{:keys [e grid] :as state}]
  (let [after-blizzard (into (vector) (map #(move % 25 120) grid))
        pos (filter #(valid? % after-blizzard) (cons e (move-candidates e 25 120)))]
    (->> pos
         (map (fn [p] {:e p :grid after-blizzard}))
         set)))

(defn keep-best-states [states]
  (->> states
       (sort-by #(+ (- 25 (:x (:e %))) (- 120 (:y (:e %)))))
       (take 50)))

(defn next-minute [states]
  (->> states
       (map next-states)
       (reduce set/union)))

(defn part1 []
  (loop [state (next-minute #{init-state})
         min 1]
    ;(println min)
    (if (contains? (set (map :e state)) {:x 24 :y 119})
      (inc min)
      (recur (next-minute state) (inc min)))))

(defn part2 [grid start goal]
  (let [init-state {:e start :grid grid}]
    (loop [states (next-minute #{init-state})
           min 1]
      (println min)
      (if (contains? (set (map :e states)) goal)
        {:mins (inc min) :grid (:grid (first (filter #(= goal (:e %)) states)))}
        (recur (next-minute states) (inc min))))))

(defn solve2 []
  (let [res1 (part2 (:grid init-state) {:x -1 :y 0} {:x 24 :y 119})
        res2 (part2 (:grid res1) {:x 25 :y 119} {:x 0 :y 0})
        res3 (part2 (:grid res2) {:x -1 :y 0} {:x 24 :y 119})]
    (println (:mins res1))
    (println (:mins res2))
    (println (:mins res3))
    (+ (:mins res1) (dec (:mins res2)) (dec (:mins res3)))))