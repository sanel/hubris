(ns hubris.builtin
  "Builtin hubris commands."
  (:gen-class)
  (:use hubris.command)
  (:use hubris.repl)
  (:use clojure.contrib.str-utils)
  (:import [org.apache.hadoop.hbase.client Scan HTable]
           [org.apache.hadoop.hbase.filter FirstKeyOnlyFilter]
           [org.apache.hadoop.hbase.util Bytes])
  (:require hbase.core)
  ;; renaming builtin commands as same names are used by the shell
  (:refer-clojure :rename {count core-count
                           get   core-get}))

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
        (println "No such command") )))

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
    ([host zk] (hbase.core/connect-to host zk)))

  (defcommand host
    "Return name of host we are connected to."
    []
    (let [hh (hbase.core/hbase-host)]
      (if hh
        (printf "Connected to '%s'\n" hh)
        (println "Not connected to any HBase instance")
  ) ) )

  (defcommand create
    "Create table; pass table name, a dictionary of specifications per column family, and optionally
a dictionary of table configuration. Dictionaries are described below in the GENERAL NOTES section.

Examples:

  hubris> create \"t1\" {:NAME \"f1\" :VERSIONS 5}
  hubris> create \"t1\" [{:NAME \"f1\"} {:NAME \"f2\"} {:NAME \"f3\"}]
  hubris> ;; The above in shorthand would be the following:
  hubris> create \"t1\" \"f1\" \"f2\" \"f3\"
  hubris> create \"t1\" {:NAME \"f1\" :VERSIONS 1 :TTL 2592000 :BLOCKCACHE true}"
   [table opts]
   (println "TODO"))

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

  (defcommand truncate
    "Disables, drops and recreates the specified table."
    [table]
    (hbase.core/with-connection
      ;; first we must get descriptor, as deleting table will remove it
      (let [tt (new HTable table)
            td (.getTableDescriptor tt)]

        (printf "Truncating %s; it may take a while\n" table)
        (when (hbase.core/table-enabled? table)
          (println "Disabling table...")
          (hbase.core/disable-table table))

        (println "Dropping table...")
        (.deleteTable (hbase.core/hbase-admin) table)
        (println "Creating table...")
        (.createTable (hbase.core/hbase-admin) td) )))

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
                (recur iter cc))
              ;; else
              (printf "%s row(s)\n" rcount)
  ) ) ) ) ) )

  (defcommand scan
    "Scan a table; pass table name and optionally a dictionary of scanner specifications.  Scanner specifications may include one or 
more of the following: LIMIT, STARTROW, STOPROW, TIMESTAMP, or COLUMNS.  If no columns are specified, all columns will be scanned.
To scan all members of a column family, leave the qualifier empty as in 'col_family:'.  

Examples:
  hubris> scan \".META.\"
  hubris> scan \".META.\" {:COLUMNS \"info:regioninfo\"}
  hubris> scan \"t1\" {:COLUMNS [\"c1\", \"c2\"], :LIMIT 10, :STARTROW \"xyz\"}
           
For experts, there is an additional option -- CACHE_BLOCKS -- which switches block caching for the scanner on (true) or off (false).
By default it is enabled.

Examples:
  hubris> scan \"t1\", {:COLUMNS [\"c1\", \"c2\"], :CACHE_BLOCKS false}"
    ([table] (scan table {}))
    ([table options]
      (hbase.core/with-connection
        (let [limit       (core-get options :LIMIT -1)
              maxlength   (core-get options :MAXLENGTH -1)
              scan-filter (core-get options :FILTER)
              startrow    (core-get options :STARTROW "")
              stoprow     (core-get options :STOPROW "")
              timestamp   (core-get options :TIMESTAMP)
              cache       (core-get options :CACHE_BLOCKS true)
              versions    (core-get options :VERSIONS 1)
              columns-tmp (core-get options :COLUMNS)
              scan        (new Scan (.getBytes startrow) 
                                    (if stoprow 
                                      (.getBytes stoprow) 
                                      nil))
              ;; check what colums type is
              columns     (cond
                            (= nil columns-tmp)
                              (hbase.core/get-all-columns table)
                            (= java.lang.String (type columns-tmp))
                              [columns-tmp]
                            ;; seq type
                            (= clojure.lang.PersistentVector (type columns-tmp))
                              columns-tmp
                            :else
                              (throw (new java.lang.Exception "Accepted argumens can be only string or sequence")))
              t             (new HTable (hbase.core/hbase-conf) table)]
          ;; first add all columns
          (doseq [c columns]
            (.addColumns scan c))

          (doto scan
            (.setFilter scan-filter)
            (.setCacheBlocks cache)
            (.setMaxVersions versions))

          (if timestamp
            (.setTimestamp scan))

          (let [scanner (.getScanner t scan)
                iter    (.iterator scanner)
                rcount  0]
            (loop [iter   iter
                   rcount rcount]
              (when (.hasNext iter)
                (let [n  (.next iter)
                      cc (inc rcount)]
                  ;; make sure we obey the limit
                  (when (or (= limit -1 )
                            (< cc limit))
                    (let [rrow (Bytes/toStringBinary (.getRow n))]
                      (doseq [kv (.list n)]
                        (printf " %-20s column=%s:%s, timestamp=%s, value=%s\n", rrow
                                                                                 (new String (.getFamily kv))
                                                                                 (Bytes/toStringBinary (.getQualifier kv))
                                                                                 (str (.getTimestamp kv))
                                                                                 (Bytes/toStringBinary (.getValue kv)) )))
                    ;; next
                    (recur iter cc) )))))
  ) ) ) )

  (defcommand shutdown
    "Shut down the cluster."
    []
    (hbase.core/shutdown-cluster))

  (defcommand version
    "Output this HBase version"
    []
    (printf "Version: %s\n" (hbase.core/hbase-version)))
)
