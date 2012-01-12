(ns hubris.command
  "Functions for handling hubris actions (aka commands)."
  (:gen-class)
  (:use clojure.contrib.with-ns))

;; here are store registered commands, but to resolve each of them make sure to 
;; use 'hubris.command' namespace, as there symbols are stored by default
(def *known-commands* (ref []))

(defn command-exists? 
  "Check if command is already defined."
  [cmd]
  (some #(= (str %) 
            (str cmd)) 
    @*known-commands*))

(defn register-command [cmd]
  (dosync
    (if (command-exists? cmd)
      (printf "'%s' is already defined\n" cmd)
      (ref-set *known-commands* (conj @*known-commands* cmd))
) ) )

(defn clear-commands
  "Unregister all commands."
  []
  (dosync
    (ref-set *known-commands* [])
) )

(defn count-commands
  "Size of registered commands."
  []
  (count @*known-commands*))

(defn all-commands
  "Return seq of all registered commands."
  []
  @*known-commands*)

(defn command-doc
  "Return associated documentation for command or nil if not exists."
  [cmd]
  ;; only 'println' is in clojure namespace
  (if (= cmd 'println)
    (:doc (meta (intern 'clojure.core cmd)))
    (:doc (meta (intern 'hubris.builtin cmd)))
) )

(defmacro defcommand
  "Creates hubris command."
  [name args & body]
  `(do
     (register-command '~name)
     (with-ns 'hubris.builtin
       (defn ~name ~args
         ~@body))
)  )

(defmacro make-builtin 
  "Creates builtin command. For now, nothing too smart, but 
  I have big plans for the future :P."
  [name]
  `(register-command '~name))
