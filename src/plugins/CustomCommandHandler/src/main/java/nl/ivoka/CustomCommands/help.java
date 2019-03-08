package nl.ivoka.CustomCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;

public class help implements CustomCommand {
    public help(CommandEvent e, CustomCommandHandler c) { help(); }

    public void help() {
        new listeners(null, null).help();
        new config(null, null).help();
        new commandlisteners(null, null).help();
    }
}
