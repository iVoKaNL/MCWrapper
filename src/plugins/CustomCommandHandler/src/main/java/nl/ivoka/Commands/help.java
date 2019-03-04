package nl.ivoka.Commands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;

public class help implements CustomCommand {
    private CustomCommandHandler c;
    private String[] args;

    public help(CommandEvent e, CustomCommandHandler c) { help(); }

    public void help() {
        new listeners(null, null).help();
        new config(null, null).help();
    }
}
