package nl.ivoka.events.mcwrapper;

import nl.ivoka.events.EventArgs;

public class CommandEventArgs extends EventArgs {
    public final String command;
    public final String[] args;

    public CommandEventArgs(String line) {
        command = line.substring(1).split("\\s+")[0];
        args = line.substring(command.length()+1).trim().split("\\s+");

        //if (args[0].isEmpty())
        //    args = new String[0];
    }

    @Override
    public String toString() {
        return "CommandEventArgs -"+
                " command:"+command+
                " args:"+args.toString();
    }
}
