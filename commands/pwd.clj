
(defcommand pwd
  "Print current directory."
  []
  (.getCanonicalPath (java.io.File. ".")))
