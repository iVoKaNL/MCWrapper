package nl.ivoka.JavascriptCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.JavascriptConnector;

public class help implements CustomCommand {
    public help(CommandEvent e, JavascriptConnector javascriptConnector) { help(); }

    public void help() {
        new javascriptconnector(null,null).help();
    }
}
