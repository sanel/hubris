;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.builtin
  "Builtin hubris commands."
  (:gen-class)
  (:use hubris.command)
  (:use hubris.repl)
  (:use clojure.contrib.str-utils)
  (:require hbase.core))

(defn register-all 
  "Register all builtin commands. Done at application startup."
  []

  (defcommand help
    "Show help."
    []
    (doseq [cmd (all-commands)]
      ;; now, see if we have to adjust spaces due possible multiple lines
      ;; in documentation string
      (let [lines (re-split #"\n" (command-doc cmd))]
        (printf " %-20s %s\n" cmd (first lines))
        (doseq [line (rest lines)]
          (printf " %-20s %s\n" " " line))
        (println ""))
  ) )

  (defcommand exit
    "Exit from shell."
    []
    (System/exit 0))

  ;; println is special as is refered to clojure content
  (make-builtin println)

  (defcommand clojure-mode-on
    "Go into clojure mode. In this mode you have full access to clojure shell and language."
    []
    (println "You are now in clojure mode. To return back, type '(hubris.repl/clojure-mode false)'.")
    (clojure-mode true))

  (defcommand connect
    "Connect to given host. If not given host parameter, it is assumed 'localhost' to be used.
Also you can specify zookeeper address, which is by default used address from connection host.

Examples:
  hubris> connect                  ;; connect to localhost
  hubris> connect \"foo\"          ;; connect to 'foo' host and the same zookeeper address
  hubris> connect \"foo\" \"baz\"  ;; connect to 'foo' host with 'baz' as zookeeper address"
     ([]     (connect "localhost"))
     ([host] (connect host host))
     ([host zk] (hbase.core/connect-to host zk))
  )

  (defcommand list-tables
    "List all tables in hbase"
    []
    (if (hbase.core/connected?)
      (doseq [i (hbase.core/list-tables)]
        (println i))
      (println "Not connected to HBase")
  ) )

  (defcommand exists
    "Does the named table exist? e.g. 'hbase> exists \"t1\"'"
    [name]
    (hbase.core/table-exists? name))
)
