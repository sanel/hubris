;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hbase.core
  "Code for interfacing with HBase. Unfortunately, all available clojure
  wrappers for HBase are pretty much unusable for hubris needs."
  (:gen-class)
  (:import [org.apache.hadoop.hbase HBaseConfiguration MasterNotRunningException]
           [org.apache.hadoop.hbase.client HBaseAdmin HTable Put Get Scan]))

(def *hbase-admin* (ref nil))
(def *hbase-conf*  (ref nil))

(defn table-name
  "Return table name from descriptor."
  [descriptor]
  (.getNameAsString descriptor))

;; TODO: make this function lazy
(defn list-tables
  "Return list of table names."
  []
  (when @*hbase-admin*
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
  ) )

  (try
    (dosync
      (ref-set *hbase-admin* (new HBaseAdmin @*hbase-conf*)) )
    (catch MasterNotRunningException e
      (printf "*** Master is not running (%s)\n" (.getMessage e))
) ) )

(defn disconnect
  "Clears connection refs."
  []
  (dosync
    (ref-set *hbase-admin* nil)
    (ref-set *hbase-conf*  nil)
) )

(defn connected?
  "Return true if connected."
  []
  (if (and @*hbase-conf* @*hbase-admin*)
    true
    false)
)
