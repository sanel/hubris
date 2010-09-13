;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.builtin
  "Builtin hubris commands."
  (:gen-class)
  (:use hubris.command)
  (:use hubris.repl))

(defn exit
  "Exit from shell."
  []
  (System/exit 0))

(defn help
  "Show help."
  []
  (doseq [cmd (all-commands)]
    (printf " %-20s %s\n" cmd "Here should go help"))
)

(defn clojure-mode-on
  "Go into Clojure mode."
  []
  (println "You are now in Clojure mode. To return back, type '(hubris.repl/clojure-mode false)'.")
  (clojure-mode true))

(defn register-all 
  "Register all builtin commands. Done at application startup."
  []
  (make-builtin help)
  (make-builtin exit)
  ;; commands to be executed in sandbox
  (make-builtin println)
  (make-builtin clojure-mode-on))
