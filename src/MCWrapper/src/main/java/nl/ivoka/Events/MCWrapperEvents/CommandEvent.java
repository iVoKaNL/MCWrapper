package nl.ivoka.Events.MCWrapperEvents;

import nl.ivoka.Events.EventArgs;

public class CommandEvent extends EventArgs {
    public String command;
    public String[] args;

    public CommandEvent(String line) {
        command = line.substring(1).split("\\s+")[0];
        args = line.substring(command.length()+1).trim().split("\\s+");

        if (args[0].isEmpty())
            args = new String[0];
    }
}
