(ns hubris.command-dir
  "Code for loading commands from specific directory."
  (:gen-class))

(defn load-dir [fd dir ext]
  (doseq [i (.list fd)]
    (let [full-path (str dir 
                         java.io.File/separator 
                         i)]
      (when (.endsWith i ext)
        (printf "Loading %s...\n" full-path)

        ;; so changes are kept localy
        (binding [*ns* *ns*]
          (in-ns 'hubris.builtin)
          (load-file full-path)
) ) ) ) )

(defn load-all
  "Load all files with .clj extension from given directory
   and evaluate each of them."
  [dir]
  (let [fd (new java.io.File dir)]
    (if (.exists fd)
      (load-dir fd dir ".clj")
      (printf "'%s' is not readable. hubris is started with only builtin commands\n" dir)
) ) )
