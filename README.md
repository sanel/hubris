# Hubris

Hubris is abbreviation for _Hbase UBeR Interface Shell_ and is [HBase](http://hbase.org) shell
on steroids, providing alternative to quite limited _hbase shell_ command.

Key features:
    * speed - much faster startup and runtime than JRuby version, as code is compiled to java bytecode
    * familiar - execute comands as you did in old _hbase shell_
    * extendible - you can easily add your own commands, by simply writing [Clojure](http://clojure.org) script and putting it in appropriate directory
    * connectable - connect it to local HBase instance or remote by simply running _hubris> connect <host>_
    * ... (here should comes other features after implemented ;))

## Usage

After archive unpacking, executing

    > <hubris-install-dir>/bin/hubris

would start the shell. Simply add _--help_ to see more options, or type _help_ in shell
to see available commands.

## Installation

To install hubris, simply unpack archive in desired directory. All required files are expected
to be in hubris installation directory, which is true by default.

## License

FIXME: write
