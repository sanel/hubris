(ns hubris.repl
  (:gen-class)
  (:require clojure.main)
  (:require hubris.command))

(defn expr-is-quoted?
  "Return true if given expression is quoted"
  [expr]
  (or
    (re-find #"(^'.*'$)" expr)
    (re-find #"(^\".*\"$)" expr)))

(defn single-to-double-quotes
  "Try to replace \"'\" with \"\"\" so clojure can evaluate it"
  [expr]
  (if (re-find #"'.*'" expr)
    (.replace expr "'" "\"")
    expr))

(defn evaluator
  "Turn given string it into clojure expression and evaluate it."
  [expr]
  (let [expr (single-to-double-quotes expr)]
    (if (expr-is-quoted? expr)
      ;; quoted expression simply pass to clojure
      (eval expr)
      ;; make sure expression exists first; this is checked by taking first token from it
      ;; as it always contains function name, e.g. 'println "This is foo"' => 'println'
      (let [func (first (.split expr " "))]
        (if (hubris.command/command-exists? func)
          (let [s (str "(" expr ")")
                e (read-string s)]
            (eval e))
          ;; else
          (printf "Unknown command: '%s'\n" func)
) ) ) ) )

(defn evaluate-with-redirection
  "Scan expression for possible redirection(s), and if found, bind output to it. If not, proceed as usual."
  [expr]
  ;; Try to catch CTRL-D input; normaly, REPL would quit with CTRL-C, but CTRL-D combo would force nil input
  ;; causing further functions to throw null exception. I'm hoping this 'hack' would not cause other troubles...
  (when-not expr
    (System/exit 0))

  (if (re-find #"\s+>\s+" expr)
    (do
      (let [tokens (.split expr "\\s+>\\s+")
            len    (count tokens)]
        (cond
          (= len 1)
            (println "Missing redirection argument")
          (= len 2)
            (let [eexpr  (nth tokens 0)
                  ;; trim possible whitespaces so filename does not contains them
                  output (.trim (nth tokens 1))]
              (with-open [stream (new java.io.FileWriter output)]
                (binding [*out* stream]
                  (evaluator eexpr) )))
          :else
            (println "Ambiguous redirection. Only single redirection is supported") )))
    ;; else directly evaluate it
    (evaluator expr)
) )

(defn init-prompt
  "Called when prompt is initialized."
  []
  (in-ns 'hubris.builtin)
  (println "Hbase UBer Interactive Shell; enter 'help<RETURN>' to see available commands or 'exit<RETURN>' to quit."))

(defn repl-prompt
  "Hubris prompt for plain mode."
  []
  (print "hubris> "))

(defn repl-reader
  "Expression reader. It uses string value returned by default reader."
  [prompt exit]
  (.readLine *in*))

(defn repl-print
  "Printer for plain expressions; it will not print nil object."
  [e]
  (if e (prn e))
)

(defn init-plain-repl
  "Initialize REPL suitable for entering non sexp expressions. Expressions must
  be one-liners and will be transformed into Clojure expressions, calling existing
  functions. E.g.

   clojure=> println \"This is simple expression\""
  []
  (clojure.main/repl
    :init     init-prompt
    :prompt   repl-prompt
    :read     repl-reader
    :print    repl-print
    :eval     evaluate-with-redirection)
)

(defn clojure-mode
  "Set mode. If is true, Clojure REPL is loaded; if not, hubris REPL with simplified language
  is used."
  [on]
  (if on
    (clojure.main/main)
    (init-plain-repl)
) )
