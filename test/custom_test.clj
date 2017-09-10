(ns custom-test
  (:require [clojure.test :refer :all]
            [logical-interpreter :refer :all]))

(def database "
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
  hijo(X, Y) :- varon(X), padre(Y, X).
  hija(X, Y) :- mujer(X), padre(Y, X).
")

(deftest custom-fact-test
  (testing "varon(juan). should be true"
    (is (= (evaluate-query database "varon(juan).")
           true))) 
  (testing "varon(hector). should be true"
    (is (= (evaluate-query database "varon(hector).")
           true))) 
  (testing "varon(maria). should be false"
    (is (= (evaluate-query database "varon(maria).")
           false))) 
  (testing "mujer(cecilia). should be true"
    (is (= (evaluate-query database "mujer(cecilia).")
           true))))

(deftest parent-database-rule-test
  (testing "hijo(pepe, juan) should be true"
    (is (= (evaluate-query database "hijo(pepe, juan)")
           true)))
  (testing "hija(maria, roberto) should be false"
    (is (= (evaluate-query database "hija(maria, roberto)")
           false))))
