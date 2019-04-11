(ns game-of-life.core
  (:require [clojure.set :as set])
  (:require [clojure.pprint :as pp])
  (:require [quil.core :as q])
  (:require [quil.middleware :as m])
  (:gen-class))

(defn generate-random-cell-world
  [height width]
  (assoc {}
    :cells (set
             (remove nil?
                     (for [y (range height)
                           x (range width)]
                       (if (> (rand) 0.5)
                         (vector y x)))))
    :height height
    :width width)
  )

; conway's game of life: Indexes of Cell neighborhood
(def neighborhood-indexes-moore
  (for [y (range -1 2)
        x (range -1 2)
        :when (not (and (= x 0) (= y 0)))]
    (vector y x)))

; conway's game of life: rules for staying alive or becoming alive
(def alive->alive? #{3 2})
(def dead->alive? #{3})

(defn get-live-cells
  [cells {cell_world :cells}]
  (set/intersection cells cell_world))

(defn get-difference-between2cell-populations
  [cells_a cells_b]
  (set/difference cells_a cells_b))

(defn wrap-world-around
  [wrap_on_value val cell_world]
  (mod val (wrap_on_value cell_world)))

(defn get-neighbourhood
  ([cell cell_world]
   (set
     (map (fn [[y x]] [(wrap-world-around :height (+ (first cell) y) cell_world)
                       (wrap-world-around :width (+ (last cell) x) cell_world)])
          neighborhood-indexes-moore))))

(defn get-dead-cells-neighbouring-alive-cells
  [{:keys [cells] :as cell_world}]
  (get-difference-between2cell-populations
    (into #{} (apply concat (map #(get-neighbourhood % cell_world) cells)))
    cells)
  )

(defn process-cell
  "
  Process a cellular automaton (cell) based on the neighbouring cells and an update cell-update-rule-fn.
  Returns the cell if it makes to the next tick or nil if it doesn't
  "
  [cell cell_world cell-update-rule-fn]
  (-> cell
      (#(get-neighbourhood % cell_world))
      (get-live-cells cell_world)
      (count)
      (cell-update-rule-fn)
      (and cell)
      )
  )

(defn process-cells
  [cells cell_world cell-update-rule-fn]
  (set
    (remove nil?
            (map #(process-cell % cell_world cell-update-rule-fn) cells)))
  )


(defn next-tick-update-alive-dead-cells
  [{:keys [cells] :as cell_world}]
  (set/union
    (process-cells cells cell_world alive->alive?)          ; deciding which live cells will make it to next round
    (process-cells (get-dead-cells-neighbouring-alive-cells cell_world) cell_world dead->alive?) ;deciding which dead cells will resurrect
    )
  )

(defn next-tick
  [cell_world]
  (assoc {}
    :cells (next-tick-update-alive-dead-cells cell_world)
    :height (:height cell_world)
    :width (:width cell_world))
  )

(defn game-of-live-lazy-seq
  [initial_world]
  (lazy-seq
    (let [next_world (next-tick initial_world)]
      (cons (next-tick next_world) (game-of-live-lazy-seq next_world)))
    )
  )


(defn run-conway
  ([how_may_generations initial_cell_world]
   "Take x samples from the conway seq"
   (take (+ 1 how_may_generations) (game-of-live-lazy-seq initial_cell_world))))


(defn setup []
  (q/frame-rate 10)
  (q/background 0)
  (generate-random-cell-world 80 80))


(defn draw-state [cell_world_state]
  (q/background 0)
  (q/blend-mode :replace)
  (q/stroke 1)
  (q/stroke-weight 1)
  (q/fill 255)
  (doseq [[y x] (map identity (:cells cell_world_state))]
    (q/rect (* y 10) (* x 10) 10 10)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (q/defsketch game-life
               :title "Game of life"
               :settings #(q/smooth 2)
               :setup setup
               :size [800 800]
               :update next-tick
               :draw draw-state
               :middleware [m/fun-mode]
               )
  )

