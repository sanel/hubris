# Shell Exercises

This article is inspired from [HBase Book Exercises](http://hbase.apache.org/book/quickstart.html#shell_exercises).

Connect to your running HBase via **hubris**.

    $ ./bin/hubris
    Hbase UBer Interactive Shell; enter 'help<RETURN>' to see available commands or 'exit<RETURN>' to quit.
    hubris>

Type help and then <RETURN> to see a listing of shell commands and options. Browse at least the paragraphs at
the end of the help emission for the gist of how variables and command arguments are entered into the HBase shell;
in particular note how table names, rows, and columns, etc., must be quoted.

Create a table named test with a single column family named cf. Verify its creation by listing all tables and then
insert some values.

    hubris> create 'test', 'cf'
	true
	hubris> list-table 'test'
	..

_TODO_
