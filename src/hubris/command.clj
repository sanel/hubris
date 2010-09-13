;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.command
  (:gen-class))

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

(defmacro command-doc
  "Return associated documentation for command or nil if not exists."
  [cmd]
  `(:doc (meta (var ~cmd))))

(defmacro defcommand
  "Creates hubris command."
  [name args & body]
  `(do
     (register-command '~name)
     (defn ~name ~args
       ~@body)
))

(defmacro make-builtin 
  "Creates builtin command. For now, nothing too smart, but 
  I have big plans for the future :P."
  [name]
  `(register-command '~name))
