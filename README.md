# Hubris

Hubris is abbreviation for _Hbase UBeR Interface Shell_ and is [HBase](http://hbase.org) shell
on steroids, providing alternative to quite limited _hbase shell_ command.

Key features:

* speed - much faster startup and runtime than JRuby version, as code is compiled to java bytecode
* familiar - execute commands as you did in old _hbase shell_
* extendable - you can easily add your own commands, writing [Clojure](http://clojure.org) script and putting it in appropriate directory. Check
[Extending Hubris](http://github.com/sanel/hubris/blob/master/EXTENDING.md).
* connectable - connect to local HBase instance or remote by simply running _connect HOSTNAME_
* ... (here should comes other features after they gets implemented ;))

## Usage

To start hubris shell, simply run

    $ <hubris-install-dir>/bin/hubris

Running _help_ inside shell would give you available commands you can use, like:

    Hbase UBeR Interactive Shell; enter 'help<RETURN>' to see available commands or 'exit<RETURN>' to quit.
    hubris> help<ENTER>

    help                 Show help.

    exit                 Exit from shell.

    println              Same as print followed by (newline)

    clojure-mode-on      Go into clojure mode. In this mode you have full access to clojure shell and language.

    ...

For now, the main difference from _hbase shell_ is that you explicitly must connect to database, buy running **connect**
command. If executed without parameters, it will try to connect to HBase served on localhost; otherwise on host given
as parameter.

Hubris provides two modes:

* plain mode - you executed commands in form as you did in _hbase shell_
* clojure mode - gives you access to Clojure REPL, from where you can use hubris or HBase API by writing Clojure code

By default, hubris starts in plain mode and typing 'clojure-mode-on' will switch you to Clojure mode, like

    hubris> clojure-mode-on
    You are now in clojure mode. To return back, type '(hubris.repl/clojure-mode false)'.
    Clojure 1.1.0
    user=> 

As message indicate, executing **(hubris.repl/clojure-mode false)** will return you back to plain mode.

## Installation

Hubris comes in two versions:

* normal - without bundled dependencies (Clojure, Hadoop, Hbase and etc.)
* big - with all dependencies

Normal version is marked as _hubris-VERSION.tar.gz_ and the big one as _hubris-VERSION-full.tar.gz_.

To use normal version, you must create _lib_ directory inside unpacked hubris folder, and copy clojure, clojure-contrib,
hadoop/hbase (with all their dependencies) jars. Without this operation, you would get this error:

    Exception in thread "main" java.lang.NoClassDefFoundError: clojure/lang/IFn
    Caused by: java.lang.ClassNotFoundException: clojure.lang.IFn
            at java.net.URLClassLoader$1.run(URLClassLoader.java:217)
            at java.security.AccessController.doPrivileged(Native Method)
            at java.net.URLClassLoader.findClass(URLClassLoader.java:205)
            at java.lang.ClassLoader.loadClass(ClassLoader.java:321)
            at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:294)
            at java.lang.ClassLoader.loadClass(ClassLoader.java:266)
    Could not find the main class: hubris.core. Program will exit

This means required jars (in this case clojure jars) are not found.

Alternative to copying all these dependencies is to simply export _CLASSPATH_ with all jars for clojure, clojure-contrib,
hadoop and hbase; hubris will happily pick them up. Or, if you would like to use different folder or location than _lib_ in
currently unpacked folder, simply export _HUBRIS_LIB_ to target location.

With explicitly setting dependent jars, hubris can reuse already installed libraries, without providing duplicate one.

If you are not comfortable with this feature or find problem using normal version, just download the big one (_hubris-VERSION-full.tar.gz_).

## License

Copyright (c) Sanel Zukan. The project is licensed under EPL 1.0.
