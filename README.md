# Hubris

Hubris is abbreviation for _Hbase UBeR Interface Shell_ and is [HBase](http://hbase.org) shell
on steroids, providing alternative to quite limited _hbase shell_ command.

Key features:

* speed - much faster startup and runtime than JRuby version, as code is compiled to java bytecode
* familiar - execute commands as you did in old _hbase shell_
* extendible - you can easily add your own commands, writing [Clojure](http://clojure.org) script and putting it in appropriate directory. Check
[Extending Hubris](http://github.com/sanel/hubris/blob/master/EXTENDING.md).
* connectible - connect to local HBase instance or remote by simply running _connect HOSTNAME_
* ... (here should comes other features after they gets implemented ;))

## Usage

To start hubris shell, simply run

    > <hubris-install-dir>/bin/hubris

Running _help_ inside shell would give you available commands you can use, like:

    Hbase UBer Interactive Shell; enter 'help<RETURN>' to see available commands or 'exit<RETURN>' to quit.
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

To install hubris, simply unpack archive in desired directory.

## License

FIXME: write
