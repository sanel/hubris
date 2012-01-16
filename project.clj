(defproject hubris "0.1.0"
  :description "Hubris - HBase uber shell"
  :license "EPL 1.0"
  :url "http://github.com/sanel/hubris"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.apache.hbase/hbase "0.90.0"]
                 [org.apache.hadoop/hadoop-core "0.20.2"]
                 [org.apache.zookeeper/zookeeper "3.3.2"]]
  :main hubris.core)
