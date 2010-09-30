;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hbase.core
  "Code for interfacing with HBase. Unfortunately, all available clojure
  wrappers for HBase are pretty much unusable for hubris needs."
  (:gen-class)
  (:import [org.apache.hadoop.hbase HBaseConfiguration MasterNotRunningException]
           [org.apache.hadoop.hbase.client HBaseAdmin HTable Put Get Scan]
           [org.apache.hadoop.hbase.util VersionInfo]))

(def *hbase-admin* (ref nil))
(def *hbase-conf*  (ref nil))

(defmacro with-connection
  "Perform action if connected to HBase, or plop error message if not."
  [& body]
  `(if (and @*hbase-admin* @*hbase-conf*)
     (do ~@body)
     (do
       (println "Not connected to database")
       nil)
) )

(defn table-name
  "Return table name from descriptor."
  [descriptor]
  (.getNameAsString descriptor))

;; TODO: make this function lazy
(defn list-tables
  "Return list of table names."
  []
  (with-connection
    (map table-name (.listTables @*hbase-admin*))
) )

(defn connect-to
  "Connect to given host with given zookeeper address."
  [host zookeeper]
  (dosync
    (ref-set *hbase-conf* (new HBaseConfiguration))
    (doto @*hbase-conf*
      (.set "hbase.master" host)
      (.set "hbase.zookeeper.quorum" zookeeper)

      ;; some default values from 'hbase shell'
      (.setInt "hbase.client.retries.number" 7)
      (.setInt "ipc.client.connect.max.retries" 3)
  ) )

  ;; setup logging verbosity
  (if (= "1" (System/getenv "HUBRIS_DEBUG"))
    (do
      (.setLevel (org.apache.log4j.Logger/getLogger "org.apache.zookeeper")
                 org.apache.log4j.Level/DEBUG)
      (.setLevel (org.apache.log4j.Logger/getLogger "org.apache.hadoop.hbase")
                 org.apache.log4j.Level/DEBUG))
    (do
      (.setLevel (org.apache.log4j.Logger/getLogger "org.apache.zookeeper")
                 org.apache.log4j.Level/ERROR)
      (.setLevel (org.apache.log4j.Logger/getLogger "org.apache.hadoop.hbase")
                 org.apache.log4j.Level/ERROR)))

  (try
    (dosync
      (ref-set *hbase-admin* (new HBaseAdmin @*hbase-conf*)) )
    (catch MasterNotRunningException e
      (printf "*** Master is not running (%s)\n" (.getMessage e))
  ) )

  ;; return status
  (and @*hbase-conf* @*hbase-admin* true)
)

(defn disconnect
  "Clears connection refs."
  []
  (with-connection
    (dosync
      (ref-set *hbase-admin* nil)
      (ref-set *hbase-conf*  nil)
) ) )

(defn connected?
  "Return true if connected."
  []
  (if (and @*hbase-conf* @*hbase-admin*)
    true
    false)
)

(defn table-exists?
  "Check if given table exists."
  [name]
  (with-connection
    (.tableExists @*hbase-admin* name)
) )

(defn hbase-version
  "Return HBase version, revision and build date as string"
  []
  (str (VersionInfo/getVersion) ","
       "r" (VersionInfo/getRevision) ","
       (VersionInfo/getDate)))

(defn enable-table
  "Enable given table. First check if is enabled before"
  [table]
  (with-connection
    (.enableTable @*hbase-admin* table)))

(defn disable-table
  "Disable given table. First check if is disabled before"
  [table]
  (with-connection
    (.disableTable @*hbase-admin* table)))

(defn table-enabled?
  "Check if given table is enabled or disabled"
  [table]
  (with-connection
    (.isTableEnabled @*hbase-admin* table)))

(defn shutdown-cluster
  "Shutdown HBase cluster"
  []
  (with-connection
    (.shutdown @*hbase-admin*)))
