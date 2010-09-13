;; vim:ft=lisp:ts=3:sw=3:expandtab
(ns hubris.command-test
  (:use [hubris.command] :reload-all)
  (:use [clojure.test]))

(deftest test-count-commands
  (is (count-commands) 0)

  (defcommand foo [] 1)
  (is (count-commands) 1)

  (defcommand boo [] 1)
  (is (count-commands) 2)

  (defcommand foo [] 1)
  (is (count-commands) 2)

  (defcommand boo [] 1)
  (is (count-commands) 2)
)

(deftest test-command-exists?
  (clear-commands)
  (is (count-commands) 0)

  (is (not (command-exists? "foo")))

  (defcommand foo [] 1)
  (is (command-exists? "foo"))

  (defcommand boo [] 1)
  (is (command-exists? "boo"))

  (defcommand foo [] 1)
  (is (command-exists? "foo"))

  (is (not (command-exists? "abc")))
)
