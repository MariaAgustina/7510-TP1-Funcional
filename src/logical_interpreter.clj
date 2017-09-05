(ns logical-interpreter)

(defn evaluate-query
  
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]

  (def factsMap (hash-map "varon" (vector "varon(juan)." "varon(pepe)." "varon(hector).") ,
   							"mujer" (vector "mujer(maria)." "mujer(cecilia).") ,
   							"a" "b"))

;;   (println factsMap)
   
   (def factsForQueryRule (get factsMap (get (clojure.string/split query #"\((.*)") 0) ) )
	
;;	(println factsForRule)

	;;some devuelve nil si no encuentra el elemento en el vector, y devuelve true si lo encuentra
	;;(println(some #(= query %) factsForQueryRule))
	(if (some #(= query %) factsForQueryRule) 
		"true"
		"false"
	)

  )
