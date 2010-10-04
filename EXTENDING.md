# Extending hubris

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

    (defcommand pwd
      "Print current directory."
      []
      (.getCanonicalPath (new java.io.File ".")) )

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

    (use 'java.io.File)

    (defcommand pwd
      "Print current directory."
      []
      (.getCanonicalPath (new File ".")) )

