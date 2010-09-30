;; vim:ft=lisp:ts=3:sw=3:expandtab
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

(defn evaluator
  "Turn given string it into clojure expression and evaluate it."
  [expr]
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
) ) ) )

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
    :eval     evaluator)
)

(defn clojure-mode
  "Set mode. If is true, Clojure REPL is loaded; if not, hubris REPL with simplified language
  is used."
  [on]
  (if on
    (clojure.main/main)
    (init-plain-repl)
) )
