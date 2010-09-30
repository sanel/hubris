# Hubris

Hubris is abbreviation for _Hbase UBeR Interface Shell_ and is [HBase](http://hbase.org) shell
on steroids, providing alternative to quite limited _hbase shell_ command.

Key features:

* speed - much faster startup and runtime than JRuby version, as code is compiled to java bytecode
* familiar - execute comands as you did in old _hbase shell_
* extendible - you can easily add your own commands, by simply writing [Clojure](http://clojure.org) script and putting it in appropriate directory
* connectable - connect it to local HBase instance or remote by simply running _hubris> connect HOSTNAME_
* ... (here should comes other features after they gets implemented ;))

## Usage

To start hubris shell, simply run

    > <hubris-install-dir>/bin/hubris

Running _help_ inside shell would give you avaliable commands you can use, like:
    Hbase UBer Interactive Shell; enter \'help<RETURN>\' to see available commands or \'exit<RETURN>\' to quit.
    hubris> help<ENTER>

    help                 Show help.

    exit                 Exit from shell.

    println              Same as print followed by (newline)

    clojure-mode-on      Go into clojure mode. In this mode you have full access to clojure shell and language.

    ...



## Installation

To install hubris, simply unpack archive in desired directory. All required files are expected
to be in hubris installation directory, which is true by default.

## License

FIXME: write
