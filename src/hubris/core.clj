(ns hubris.core
  (:gen-class)
  (:use hubris.builtin)
  (:use hubris.repl)
  (:use hubris.command-dir))

(defn -main [& args]
  (register-all)
  (load-all "commands")
  (if (and args
           (= "true" (first args)))
    (clojure-mode true)
    (clojure-mode false))
)
