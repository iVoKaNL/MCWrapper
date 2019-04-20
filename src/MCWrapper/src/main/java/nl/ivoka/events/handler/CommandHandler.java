package nl.ivoka.events.handler;

import nl.ivoka.API.console.Console;
import nl.ivoka.events.EventArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandHandler extends Handler {
    private static CommandHandler instance = null;

    public static synchronized CommandHandler instance() {
        if (instance == null)
            instance = new CommandHandler();
        return instance;
    }
    private CommandHandler() {}

    private List<Integer> runningCommands = new ArrayList<>();

    @Override
    public void fireEvent(EventArgs args) { fireEvent(args, false); }
    public void fireEvent(EventArgs args, boolean allListeners) {
        if (!allListeners) {
            Integer _tmp = new Random().nextInt();
            while (runningCommands.contains(_tmp))
                _tmp = new Random().nextInt();
            final Integer identifier = _tmp;

            runningCommands.add(identifier);
            args.setIdentifier(identifier);

            eventListeners.forEach((x, y) -> {
                if (runningCommands.contains(identifier))
                    y.accept(args);
            });

            if (runningCommands.contains(identifier)) {
                Console.instance().writeLine("There was no command listener found for the given command.", Console.PREFIX.COMMANDHANDLER, Console.PREFIX.WARNING);
                runningCommands.remove(identifier);
            }
        } else
            super.fireEvent(args);
    }

    public void commandExecuted(Integer identifier) { runningCommands.remove(identifier); }
}
