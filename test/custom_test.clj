(ns custom-test
  (:require [clojure.test :refer :all]
            [logical-interpreter :refer :all]))

(def incomplete-database "
varon(juan).
varon(pepe).
varon(hector).
varon(roberto).
varon(alejandro).
mujer(maria).
mujer(cecilia).
padre(juan, pepe).
padre(juan, pepa).
padre(hector, maria).
padre(roberto, alejandro).
padre(roberto, cecilia).
")

(deftest custom-fact-test
  (testing "varon(juan). should be true"
    (is (= (evaluate-query incomplete-database "varon(juan).")
           "true"))) 
  (testing "varon(maria). should be false"
    (is (= (evaluate-query incomplete-database "varon(maria).")
           "false"))) 
  (testing "mujer(cecilia). should be true"
    (is (= (evaluate-query incomplete-database "mujer(cecilia).")
           "true"))))
