;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.core
  (:gen-class)
  (:use hubris.builtin)
  (:use hubris.repl))

(defn -main [& args]
  (register-all)
  (if (and args
           (= "true" (first args)))
    (clojure-mode true)
    (clojure-mode false))
)
