package nl.ivoka.CustomCommands;

import nl.ivoka.API.Commands.Command;
import nl.ivoka.API.Console;
import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.Handlers.CommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;

@Command
public class commandlisteners implements CustomCommand {
    private CustomCommandHandler c;
    private String[] args;

    public commandlisteners(CommandEvent e, CustomCommandHandler c) {
        if (e!=null&&c!=null) {
            this.c = c;
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (args[0].equals("list")) list();
                else if (args[0].equals("remove")) remove();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'commandlisteners':");
        writeInfo("  !commandlisteners help - Show this help menu");
        writeInfo("  !commandlisteners list - Shows a list of all command listeners");
        writeInfo("  !commandlisteners remove [listener1, listener2, ...] - Remove all command listeners, or all specified command listeners");
    }

    private void list() {
        StringBuilder message = new StringBuilder("Command listeners("+ CommandHandler.instance().getEventListenersSize()+"): ");

        for (String listener : CommandHandler.instance().getListeners()) {
            message.append(listener);
            message.append(", ");
        }

        Console.instance().writeLine(message.substring(0, message.length()-2), Console.PREFIX.INFO);
    }

    private void remove() {
        if (args.length == 1) {
            CommandHandler.instance().clearListeners();

            Console.instance().writeLine("Removed all command listeners!", Console.PREFIX.INFO);
        } else {
            for (int i=1; i<args.length; i++) {
                if (CommandHandler.instance().containsListener(args[i])) {
                    CommandHandler.instance().removeListener(args[i]);
                    Console.instance().writeLine("Removed "+args[i], Console.PREFIX.INFO);
                } else
                    Console.instance().writeLine(args[i]+" is not a command listeners!", Console.PREFIX.INFO);
            }
        }
    }
}
