;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.builtin
  "Builtin hubris commands."
  (:gen-class)
  (:use hubris.command)
  (:use hubris.repl)
  (:use clojure.contrib.str-utils)
  (:import [org.apache.hadoop.hbase.client Scan HTable]
           [org.apache.hadoop.hbase.filter FirstKeyOnlyFilter])
  (:require hbase.core)
  ;; renaming builtin commands as same names are used by the shell
  (:refer-clojure :rename {count core-count}))

(defn register-all 
  "Register all builtin commands. Done at application startup."
  []

  (defcommand help
    "List all commands and show help for each of it. If given command, show help
for only that command."
    ([]
      (doseq [cmd (all-commands)]
        ;; now, see if we have to adjust spaces due possible multiple lines
        ;; in documentation string
        (let [lines (re-split #"\n" (command-doc cmd))]
          (printf " %-20s %s\n" cmd (first lines))
          (doseq [line (rest lines)]
            (printf " %-20s %s\n" " " line))
          (println "")) ))
    ([cmd]
      (if (command-exists? cmd)
        (println (command-doc (symbol cmd)))
        (println "No such command") ))
  )

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

  (defcommand host
    "Return name of host we are connected to."
    []
    (let [hh (hbase.core/hbase-host)]
      (if hh
        (printf "Connected to '%s'\n" hh)
        (println "Not connected to any HBase instance")
  ) ) )

  (defcommand list-tables
    "List all tables in hbase"
    []
    (doseq [i (hbase.core/list-tables)]
      (if (hbase.core/table-enabled? i)
        (println i)
        (printf "%s [disabled]\n" i)
  ) ) )

  (defcommand enable
    "Enable the named table"
    [table]
    (if (hbase.core/table-enabled? table)
      (println "Table already enabled")
      (hbase.core/enable-table table)
  ) )

  (defcommand disable
    "Disable the named table: e.g. 'hubris> disable \"t1\"'"
    [table]
    (if (hbase.core/table-enabled? table)
      (hbase.core/disable-table table)
      (println "Table already disabled")
  ) )

  (defcommand exists
    "Does the named table exist? e.g. 'hubris> exists \"t1\"'"
    [name]
    (println (hbase.core/table-exists? name)))

  (defcommand count
    "Count the number of rows in a table. This operation may take a LONG time (Run '$HADOOP_HOME/bin/hadoop jar hbase.jar rowcount' 
to run a counting mapreduce job). Current count is shown every 1000 rows by default. Count interval may be optionally specified.

Examples:
  hubris> count \"t1\"
  hubris> count \"t1\", 100000"
    ([tname] (count tname 1000))
    ;; most of this code is shamelessly stolen from 'hbase shell' implementation
    ([tname interval]
      (hbase.core/with-connection
        (let [scan        (new Scan)
              scan-filter (new FirstKeyOnlyFilter)
              rcount      0
              table       (new HTable (hbase.core/hbase-conf) tname)]
          (doto scan
            (.setCaching 10)
            (.setFilter scan-filter))
          (loop [iter   (.iterator (.getScanner table scan))
                 rcount rcount]
            (if (.hasNext iter)
              (let [n    (.next iter)
                    cc   (inc rcount)]
                (if (= 0 (mod cc interval))
                  (printf "Current count: %s, row: %s\n" cc
                                                         (new String (.getRow n)) ))
                ;; else
                (recur iter cc))
              ;; else
              (printf "%s row(s)\n" rcount)
  ) ) ) ) ) )

  (defcommand shutdown
    "Shut down the cluster."
    []
    (hbase.core/shutdown-cluster))

  (defcommand version
    "Output this HBase version"
    []
    (printf "Version: %s\n" (hbase.core/hbase-version)))
)
