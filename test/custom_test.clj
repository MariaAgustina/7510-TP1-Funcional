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
  amigos(ricardo,agustin,jorge).
  amigos(pedro,agustin,jorge).
  hijo(X, Y) :- varon(X), padre(Y, X).
  hija(X, Y) :- mujer(X), padre(Y, X).
")

(deftest custom-fact-test
  (testing "varon(juan) should be true"
    (is (= (evaluate-query database "varon(juan)")
           true)))
  (testing "varon(maria) should be false"
    (is (= (evaluate-query database "varon(maria)")
           false)))
  (testing "mujer(cecilia) should be true"
    (is (= (evaluate-query database "mujer(cecilia)")
           true)))
  (testing "padre(juan, pepe) should be true"
    (is (= (evaluate-query database "padre(juan, pepe)")
           true)))
  (testing "padre(mario, pepe) should be false"
    (is (= (evaluate-query database "padre(mario, pepe)")
           false)))
  (testing "amigos(ricardo,agustin,jorge) should be true"
    (is (= (evaluate-query database "amigos(ricardo,agustin,jorge)")
           true)))
  (testing "amigos(pedro,agustin,jorge) should be true"
    (is (= (evaluate-query database "amigos(pedro,agustin,jorge)")
           true)))
  (testing "amigos(pedro,agustin,cecilia) should be false"
    (is (= (evaluate-query database "amigos(pedro,agustin,cecilia)")
           false))))

(deftest parent-database-rule-test
  (testing "hijo(pepe, juan) should be true"
    (is (= (evaluate-query database "hijo(pepe, juan)")
           true)))
  (testing "hija(maria, roberto) should be false"
    (is (= (evaluate-query database "hija(maria, roberto)")
           false))))

(deftest parent-database-empty-query-test
  (testing "varon should be nil"
    (is (= (evaluate-query database "varon")
           nil))))