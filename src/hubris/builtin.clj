;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.builtin
  "Builtin hubris commands."
  (:gen-class)
  (:use hubris.command)
  (:use hubris.repl))

(defn register-all 
  "Register all builtin commands. Done at application startup."
  []

  (defcommand help
    "Show help."
    []
    (doseq [cmd (all-commands)]
      (printf " %-20s %s\n" cmd (command-doc cmd))
  ) )

  (defcommand exit
    "Exit from shell."
    []
    (System/exit 0))

  ;; println is special as is refered to clojure content
  (make-builtin println)

  (defcommand clojure-mode-on
    "Go into Clojure mode."
    []
    (println "You are now in Clojure mode. To return back, type '(hubris.repl/clojure-mode false)'.")
    (clojure-mode true))
)
