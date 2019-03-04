package nl.ivoka.API.Commands;

import nl.ivoka.API.Console;

public interface CustomCommand {
    void help();

    default void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.INFO); }
    default void writeError(String msg) { Console.instance().writeLine(msg, Console.PREFIX.ERROR); }
    default void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.ERROR); }
}
