;;"borrowed" from https://github.com/Lokeh/advent-2019/
(ns intcode.core)

(require '[clojure.string :as string])

(declare operation)

(defn create-program [prog-string input]
  {:memory (string/split prog-string #",")
   :pointer 0
   :input (if (fn? input)
            input
            (constantly input))
   :output []
   :running? true})

(defmulti instruction (fn [opcode mode program] opcode))

(defn compute [{:keys [memory pointer input] :as program}]
  (try
    (let [[modes op] (operation memory pointer)]
      (instruction op modes program))
    (catch Exception e
      (throw (ex-info "An error occured"
                      program
                      e)))))

(defn char->int [c]
  (-> c
      (String/valueOf)
      (Integer/parseInt)))

(defn operation [memory pointer]
  (let [opstring (get memory pointer)
        modechars (drop-last 2 opstring)
        codestring (apply str (take-last 2 opstring))]
    [(map char->int modechars) (Integer/parseInt codestring)]))

(defn pad [xs n]
  (if (< (count xs) n)
    (pad (cons 0 xs) n)
    xs))

(defn parameters [memory pointer modes]
  {:pre [(vector? memory)]}
  (let [ps (subvec memory (inc pointer))
        mps (map vector modes (take (count modes) ps))]
    (for [[m p] mps]
      (case m
        0 (-> memory
              (get (Integer/parseInt p))
              (Integer/parseInt))
        1 (Integer/parseInt p)))))

(defn run [program input]
  (let [compute-seq (iterate compute (create-program program input))]
    (-> (take-while :running? compute-seq)
        (last)
        :output)))

(defmethod instruction 99
  [_ _ program]
  (assoc program :running? false))

(defmethod instruction 1
  ;; add
  [_ modes {:keys [memory pointer] :as program}]
  (let [[_ m2 m3] (pad modes 3)
        ;; always treat last parameter as immediate
        [v1 v2 rptr] (parameters memory pointer [m3 m2 1])]
    (-> program
        (update :memory assoc rptr (str (+ v1 v2)))
        (update :pointer + 4))))

(defmethod instruction 2
  ;; multiply
  [_ modes {:keys [memory pointer] :as program}]
  (let [[_ m2 m3] (pad modes 3)
        [v1 v2 rptr] (parameters memory pointer [m3 m2 1])]
    (-> program
        (update :memory assoc rptr (str (* v1 v2)))
        (update :pointer + 4))))

(defmethod instruction 3
  ;; set as input
  [_ modes {:keys [memory pointer input] :as program}]
  (let [[rptr] (parameters memory pointer [1])]
    (-> program
        (update :memory assoc rptr (input pointer))
        (update :pointer + 2))))

(defmethod instruction 4
  ;; output
  [_ modes {:keys [memory pointer] :as program}]
  (let [[v] (parameters memory pointer (pad modes 1))]
    (-> program
        (update :output conj v)
        (update :pointer + 2))))

(defmethod instruction 5
  ;; jump if true
  [_ modes {:keys [memory pointer] :as program}]
  (let [[m2 m1] (pad modes 2)
        [v1 v2] (parameters memory pointer [m1 m2])]
    (if (zero? v1)
      ;; noop
      (update program :pointer + 3)
      (-> program
          (assoc :pointer v2)))))

;; TODO standardize mode selection
(defmethod instruction 6
  ;; jump if false
  [_ modes {:keys [memory pointer] :as program}]
  (let [[m2 m1] (pad modes 2)
        [v1 v2] (parameters memory pointer [m1 m2])]
    (if (not (zero? v1))
      ;; noop
      (update program :pointer + 3)
      (-> program
          (assoc :pointer v2)))))

(defmethod instruction 7
  ;; less than
  [_ modes {:keys [memory pointer] :as program}]
  (let [[_ m2 m1] (pad modes 3)
        [v1 v2 rptr] (parameters memory pointer [m1 m2 1])]
    (-> program
        (update :memory assoc rptr (if (< v1 v2)
                                     "1" "0"))
        (update :pointer + 4))))

(defmethod instruction 8
  ;; equal to
  [_ modes {:keys [memory pointer] :as program}]
  (let [[_ m2 m1] (pad modes 3)
        [v1 v2 rptr] (parameters memory pointer [m1 m2 1])]
    (prn v1 v2 rptr)
    (-> program
        (update :memory assoc rptr (if (= v1 v2)
                                        "1" "0"))
        (update :pointer + 4))))

