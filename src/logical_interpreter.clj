(ns logical-interpreter)

(defn data-array-to-hash 
    [dataArray]

    (def keysVector(vector))
   (doseq [query dataArray]
      (def querykey (clojure.string/trim(get (clojure.string/split query #"\((.*)") 0)))
      (def keysVector (conj keysVector querykey))
      )

   (def filteredKeysVector (distinct keysVector))
  ;;(println filteredKeysVector)

   (def parsedMap (hash-map))
   (doseq [filteredKey filteredKeysVector]
      (def parsedMap (assoc parsedMap filteredKey (vector)))
    )

  (doseq [query dataArray]
    (def querykey (clojure.string/trim (get (clojure.string/split query #"\((.*)") 0 )))
    (def vectorValue (get parsedMap querykey))
    (def newVector (conj vectorValue (clojure.string/trim query)))  
    (def parsedMap (assoc parsedMap querykey newVector))    
  )
  ;;(println parsedMap)
  parsedMap
)  


(defn get-result-for-fact
      [query,factsVector]
 ;;     (println query)
 ;;     (println factsVector)
    (if (some #(= query %) factsVector)
      true
      false
    )            
)


(defn getNewQuery
      [query]
      (if (.endsWith query ")")
        (clojure.string/replace query ")" ").")
        query
      )
)

(defn clear-vector
      [vector]
  (def lastParameter(get (clojure.string/split (last vector) #"\)(.*)")0))
  (def vectorWithoutLastELement (pop vector))
  (def clearedVector (conj vectorWithoutLastELement lastParameter))
  clearedVector
)

(defn get-result-for-rule
    [query,ruleVector,parsedMap]

    (def rule (get ruleVector 0))
    (def ruleFunction (get (clojure.string/split rule #":-")0))

    (def parameters (get (clojure.string/split ruleFunction #"(.*)\(")1))
    (def queryParameters (get (clojure.string/split query #"(.*)\(")1))

    (def vectorQueryParamenters (clear-vector (clojure.string/split queryParameters #", ")))
    (def vectorParamenters (clear-vector (clojure.string/split parameters #", ")))
    
    (println vectorQueryParamenters)
    (println vectorParamenters)

    (def ruleWithTestParameters (clojure.string/replace rule "" ""))
    (doseq [parameter vectorParamenters]
     (def position (.indexOf vectorParamenters parameter))
      (def queryParameter (get vectorQueryParamenters position))
      (def ruleWithTestParameters (clojure.string/replace ruleWithTestParameters parameter queryParameter))
    )

    (def ruleWithTestParameters (clojure.string/replace ruleWithTestParameters "), " ") , "))
    (def ruleFactsComponents (clojure.string/split (get (clojure.string/split ruleWithTestParameters #":-")1) #" , "))

    (println ruleFactsComponents)
    (def resultsForAllFacts (vector))


    (doseq [factComponentQuery ruleFactsComponents]
      (def parsedFactsOrRuleVector (get parsedMap (clojure.string/trim(get (clojure.string/split factComponentQuery #"\((.*)") 0) ) ) )
       (println parsedFactsOrRuleVector)
       (println factComponentQuery)
       
       (def factResutl (get-result-for-fact (getNewQuery (clojure.string/trim factComponentQuery)) parsedFactsOrRuleVector))
     ,;  (println "result for fact")
       ;;(println factResutl)
       (if (= factResutl true)
          (def resultsForAllFacts (conj resultsForAllFacts "true"))  
          (def resultsForAllFacts (conj resultsForAllFacts "false"))  
        )
     ;;  (println resultsForAllFacts)
    )

    (if (some #(= "false" %) resultsForAllFacts) 
      false
      true
    )

)

(defn query-is-a-rule
      [query,parsedFactsOrRuleVector]


      (def firstElement (get parsedFactsOrRuleVector 0))
      (if firstElement
        (.contains firstElement ":-")
        false
      )
)

(defn is-wrong-query
    [query]
   ;; (println(re-find #"[a-zA-Z0-9_]*[\(][a-zA-Z0-9_, ]*[\)]" query)) 
    (if (re-find #"[a-zA-Z0-9_]*[\(][a-zA-Z0-9_, ]*[\)]" query)
      nil
      true
    )
)

(defn is-wrong-rule
    [rule]
    ;;TODO: Para validar bien las rules se deben parsear los facts internos
    (if  (re-find #"[a-zA-Z0-9_]*[\(][a-zA-Z0-9_, ]*[\)] :-" rule) 
      nil
      true
    )
)

(defn is-wrong-database
    [database] 
    (def databaseVector  (clojure.string/split-lines database))
    
    ;;se elimina el primer elemento que esta vacio
    (def lastPosition (.indexOf databaseVector (last databaseVector)))
    (def newDatabaseVector(subvec databaseVector 1 (+ lastPosition 1)))
    ;;(println newDatabaseVector)
    (def isARule)
    (def resultsVector (vector))
    (doseq [factOrRule newDatabaseVector]
      (def isARule (.contains factOrRule ":-"))
      (if isARule
        (def resultsVector (conj resultsVector (is-wrong-rule factOrRule)))
        (def resultsVector (conj resultsVector (is-wrong-query factOrRule))) 
      )
    )

    (if (some #(= true %) resultsVector) 
      true
      nil
    )
)

(defn evaluate-query
  
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]

  (if (or (is-wrong-query query) (is-wrong-database database))
      (do 
          (println "wrong database or query")
          nil)
      (do
          (def databaseVector (clojure.string/split-lines database))
          (def parsedmap (data-array-to-hash databaseVector))
          (def parsedFactsOrRuleVector (get parsedmap (get (clojure.string/split query #"\((.*)") 0) ) )
          ;;(println parsedFactsOrRuleVector)

          (if (query-is-a-rule query parsedFactsOrRuleVector)
            (get-result-for-rule query parsedFactsOrRuleVector parsedmap)
            (get-result-for-fact (getNewQuery query) parsedFactsOrRuleVector)
          )
      )
  )

)
