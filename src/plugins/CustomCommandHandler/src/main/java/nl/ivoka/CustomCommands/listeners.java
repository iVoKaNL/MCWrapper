package nl.ivoka.CustomCommands;

import nl.ivoka.API.Commands.Command;
import nl.ivoka.API.Console;
import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.EventArgs;
import nl.ivoka.Events.Handlers.EventHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Events.ServerEvents.ServerSaveEvent;
import nl.ivoka.Events.ServerEvents.ServerStatusEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Command
public class listeners implements CustomCommand {
    private CustomCommandHandler c;
    private String[] args;

    public listeners(CommandEvent e, CustomCommandHandler c) {
        if (e!=null&&c!=null) {
            this.c = c;
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (args[0].equals("list")) list();
                else if (args[0].equals("remove")) remove();
                else if (args[0].equals("fire")) fire();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'listeners':");
        writeInfo("  !listeners help - Show this help menu");
        writeInfo("  !listeners list - Shows a list of all event listeners");
        writeInfo("  !listeners remove [listener1, listener2, ...] - Remove all event listeners, or all specified event listeners");
        writeInfo("  !listeners fire <Event> [arg1, arg2, ...] - Fire a event of type 'Event', if arguments are given use them to create event");
    }

    private void list() {
        StringBuilder message = new StringBuilder("Event listeners("+ EventHandler.instance().getEventListenersSize()+"): ");

        for (String listener : EventHandler.instance().getListeners()) {
            message.append(listener);
            message.append(", ");
        }

        Console.instance().writeLine(message.substring(0, message.length()-2), Console.PREFIX.INFO);
    }

    private void remove() {
        if (args.length == 1) {
            EventHandler.instance().clearListeners();

            Console.instance().writeLine("Removed all event listeners!", Console.PREFIX.INFO);
        } else {
            for (int i=1; i<args.length; i++) {
                if (EventHandler.instance().containsListener(args[i])) {
                    EventHandler.instance().removeListener(args[i]);
                    Console.instance().writeLine("Removed "+args[i], Console.PREFIX.INFO);
                } else
                    Console.instance().writeLine(args[i]+" is not a event listener!", Console.PREFIX.INFO);
            }
        }
    }

    private void fire() {
        if (args.length >= 2) {
            boolean eventFound                  = false;
            boolean constructorFound            = false;
            Class<? extends EventArgs> event    = null;
            EventArgs eventArgs                 = null;
            Constructor<? extends EventArgs> constructor  = null;

            try { event = Class.forName("nl.ivoka.Events.ServerEvents."+args[1]).asSubclass(EventArgs.class); eventFound=true; }
            catch (ClassNotFoundException e) {}

            if (!eventFound) {
                try { event = Class.forName("nl.ivoka.Events.PlayerEvents."+args[1]).asSubclass(EventArgs.class); eventFound=true; }
                catch (ClassNotFoundException e) {}
            }

            if (eventFound) {
                if (args.length>=3) {
                    try {
                        constructor = event.getConstructor(String.class);
                        eventArgs = constructor.newInstance(args[2]);

                        constructorFound = true;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {}
                }

                if (!constructorFound && args.length>=3) {
                    try {
                        constructor = event.getConstructor(ServerSaveEvent.Event.class);
                        eventArgs = constructor.newInstance(ServerSaveEvent.Event.valueOf(args[2]));

                        constructorFound = true;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {}
                }

                if (!constructorFound && args.length>=3) {
                    try {
                        constructor = event.getConstructor(ServerStatusEvent.Event.class);
                        eventArgs = constructor.newInstance(ServerStatusEvent.Event.valueOf(args[2]));

                        constructorFound = true;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {}
                }

                if (!constructorFound && args.length>=4) {
                    try {
                        constructor = event.getConstructor(String.class, String.class);
                        eventArgs = constructor.newInstance(args[2], args[3]);

                        constructorFound = true;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {}
                }

                if (constructorFound)
                    EventHandler.instance().fireEvent(eventArgs);
                else
                    Console.instance().writeLine("No Event or constructor found that matched given input.", Console.PREFIX.INFO);
            } else
                Console.instance().writeLine("No such event found!", Console.PREFIX.INFO);
        } else
            help();
    }
}
