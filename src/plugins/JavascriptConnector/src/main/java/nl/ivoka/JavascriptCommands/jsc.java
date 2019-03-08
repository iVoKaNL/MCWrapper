package nl.ivoka.JavascriptCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.JavascriptConnector;

public class jsc implements CustomCommand {
    private JavascriptConnector javascriptConnector;
    private String[] args;

    public jsc(CommandEvent e, JavascriptConnector javascriptConnector) { new javascriptconnector(e, javascriptConnector); }

    public void help() { new javascriptconnector(null, null).help(); }
}
