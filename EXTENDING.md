# Extending Hubris

Hubris is able to be extended with additional commands, available for use from
hubris shell. These commands can perform HBase related or unrelated tasks, as you have
access to full Clojure REPL and language.

In case you never had a chance to work with Clojure, make sure to check excellent
[Clojure Tutorial](http://www.moxleystratton.com/article/clojure/for-non-lisp-programmers) from Moxley Stratton.
You also should check [Clojure Home Page](http://clojure.org), where you can find a lot of details behind language
look and design.

## Introduction

Writing additional commands is done by writing Clojure code and putting it in **<hubris-dir>/commands** directory. Scripts
will be loaded when hubris is started.

## Your first command

In this tiny tutorial, we are going to write simple command _pwd_, that will display current working directory. This command
will use Java API and have readable documentation, explaining what command does.

In editor write this:

```clojure
(defcommand pwd
  "Print current directory."
  []
  (.getCanonicalPath (new java.io.File ".")) )
```

and save it as **<hubris-dir>/commands/pwd.clj**. After you start hubris, you should see something like:

    Loading commands/pwd.clj...
    Hbase UBer Interactive Shell; enter 'help<RETURN>' to see available commands or 'exit<RETURN>' to quit.
    hubris> 

This means hubris successfully loaded command. You can see it by running _help_ inside hubris shell, like:

    ...
    shutdown             Shut down the cluster.

    version              Output this HBase version

    pwd                  Print current directory.

    hubris> 

By running it, you would get a full path from where hubris was executed.

## Details

**defcommand** is the main syntax for adding hubris commands. The name of command will be the same name shown in hubris
shell, and command docstring will be used to describe it in shell.

Everything after that is ordinary Clojure code with direct access to Java API, in our case _File_ class and _getCanonicalPath()_ member.

If you are going to use additional Java libraries (like HBase API), you can simply import them. Here is the same _pwd_ example, where 
we are going to import File class, instead to use it with full package name.

```clojure
(import 'java.io.File)

(defcommand pwd
  "Print current directory."
  []
  (.getCanonicalPath (new File ".")) )
```

## Commands with optional arguments

With **defcommand** you can also make a command to accept optional arguments, like:

```clojure
(defcommand print-me
  "Just print argument, or notify when there are no arguments."
  ;; called with no arguments
  ([]
    (println "No arguments"))
  ;; called with single argument
  ([arg]
    (printf "Argument is: %s\n" arg) ))
```

and save it in _<hubris-dir>/commands/print_me.clj_. When executed in hubris shell, you will get something like:

    hubris> print-me<ENTER>
    No arguments

    hubris> print-me "Olaa"<ENTER>
    Argument is: Olaa

## Making connections to HBase

Although you can directly use HBase API, hubris provides few additional methods for reusing currently made connection and
the advice is to use them. The main reason for this is when user connect to desired location (via **connect** command), your
command does not make unnecessary connections. Also, with this method, you will be able to know was that connection successful
or user tried another solution.

Here we will implement _table-exists_ command, that should print _true_ or _false_ in shell, or write some error message when
connection was not made.

```clojure
(defcommand table-exists
  "Check if given table exists."
  [table]
  (hbase.core/with-connection
    (let [admin (hbase.core/hbase-admin)
          found (.tableExists admin table)]

      (if found
        (println "true")
        (println "false") ))))
```

**with-connection** macro will make sure we are successfully connected. If not, the code will not be executed and user
will get message like _Not connected to database_.

**hbase-admin** function will return HBaseAdmin object for current connection, from where you can use 
[HBase API](http://hbase.apache.org/docs/current/api/overview-summary.html) (_tableExists_ is part of it).

Part of these functions is **hbase-conf**, returning HBaseConfiguration object.

## The Real World example

Writing own commands easily makes hubris suitable for quick operations over HBase data and tables.

Recently, I needed to fill sample database for some testing and setting ordinary project for using HBase API was
wasting of time. It didn't matter should I write it in Java, Clojure or Jruby, I still had to create project,
find appropriate bindings or create some Ant code (in Java case). And, should I mention you have to tackle with 
_hbase-site.xml_ if you are going to use default HBaseConfiguration vaules?

Instead, I created simple command (named it _do-magic_) with the following content:

```clojure
;; import needed HBase classes
(import [org.apache.hadoop.hbase.client HTable Put]
        [org.apache.hadoop.hbase.util Bytes])

;; use our wrapper and current connection objects and configuration
(require 'hbase.core)

(defcommand do-magic
  "Add million rows to 'demo2' table"
  []
  (hbase.core/with-connection

  ;; create table object using current connection and operate on 'demo2' table
  (def table (HTable. (hbase.core/hbase-conf) "demo2"))

  ;; this is the way how we loop in clojure; we start with 'i = 0' and ends when it
  ;; gets to 'e' which is million; during the iteration, we append new row with
  ;; the name 'row_X' with value X, where X is current iteration number

  ;; rest of the code is pretty much ordinary HBase API usage; create family, qualifier,
  ;; convert names/value to bytes and fill table with appropriate Put object

  (loop [i 0
         e 1000000
         familly   (Bytes/toBytes "f1")
         qualifier (Bytes/toBytes "q")]  ;; some qualifier

         (let [row   (Bytes/toBytes (format "row_%d" i))
               value (Bytes/toBytes (str i))
               p     (Put. row)]

           ;; simple status so I know how long to wait
           (printf "Adding %d\n" (inc i))

           (.add p familly qualifier value)
           (.put table p)

           ;; recurse to 'loop'
           (if (< i e)
             (recur (inc i) e familly qualifier) ) ) ) ) )
```

Job done! It took me a couple of minutes to finish this on computer where only java was installed (using hubris with all dependencies).
