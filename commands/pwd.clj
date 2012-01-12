;; example of custom command
(defcommand pwd
  "Print current directory."
  []
  (.getCanonicalPath (java.io.File. ".")))
