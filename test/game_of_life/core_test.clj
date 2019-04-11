(ns game-of-life.core-test
  (:require [clojure.test :refer :all]
            [game-of-life.core :refer :all]))


(def blinker #{[1 2] [2 2] [3 2]})
(def glider #{[2 2] [2 3] [2 4] [1 4] [0 3]})
(def block #{[0 0] [0 1] [1 0] [1 1]})
(def tub #{[0 1] [1 0] [1 2] [2 1]})
(def pentadecathlon #{[6 5] [6 6] [5 7] [7 7] [6 8] [6 9] [6 10] [6 11] [5 12] [7 12] [6 13] [6 14]})

(defn build-cell-world
  [cells]
  {
   :cells  cells
   :height 20
   :width  20
   }
  )

(defn get-cells
  [cell_world generation]
  (:cells
    (last (run-conway generation (build-cell-world cell_world))))
  )

(deftest blinker2-test
  (testing "Blinker period 2 should be equal to period 0."
    (is (= (get-cells blinker 0) (get-cells blinker 2)))))

(deftest blinker1-test
  (testing "Blinker period 1 should not be equal to period 0"
    (is (not= (get-cells blinker 0) (get-cells blinker 1)))))

(deftest glider80-test
  (testing "Glider period 80 for a 20x20 world should be equal to period 0."
    (is (= (get-cells glider 0) (get-cells glider 80)))))

(deftest glider2-test
  (testing "Glider period 2 for a 20x20 world should not be equal to period 0."
    (is (not= (get-cells glider 0) (get-cells glider 2)))))

(deftest block2-test
  (testing "Block of period X world should be equal to period 0."
    (is (= (get-cells block 0) (get-cells block 2)))))

(deftest block3-test
  (testing "Block of period X world should be equal to period 0."
    (is (= (get-cells block 0) (get-cells block 3)))))

(deftest tub2-test
  (testing "Tub of period X world should be equal to period 0."
    (is (= (get-cells tub 0) (get-cells tub 2)))))

(deftest tub3-test
  (testing "Tub of period X world should be equal to period 0."
    (is (= (get-cells tub 0) (get-cells tub 3)))))

(deftest pentadecathlon15-test
  (testing "pentadecathlon of period 15 world should be equal to period 0."
    (is (= (get-cells pentadecathlon 0) (get-cells pentadecathlon 15)))))

(deftest pentadecathlon14-test
  (testing "pentadecathlon of period 14 world should not be equal to period 0."
    (is (not= (get-cells pentadecathlon 0) (get-cells pentadecathlon 14)))))
