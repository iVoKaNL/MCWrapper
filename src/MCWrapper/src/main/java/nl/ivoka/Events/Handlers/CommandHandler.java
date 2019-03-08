package nl.ivoka.Events.Handlers;

import nl.ivoka.API.Console;
import nl.ivoka.Events.EventArgs;

import java.util.*;

public class CommandHandler extends Handler {
    private static CommandHandler instance = null;

    public static synchronized CommandHandler instance() {
        if (instance == null)
            instance = new CommandHandler();
        return instance;
    }
    private CommandHandler() {}

    private List<Integer> runningCommand = new ArrayList<>();

    @Override
    public void fireEvent(EventArgs args) { fireEvent(args, false); }
    public void fireEvent(EventArgs args, boolean allCommands) {
        if (!allCommands) {
            Integer _identifier = new Random().nextInt();

            while(runningCommand.contains(_identifier))
                _identifier = new Random().nextInt();

            final Integer identifier = _identifier;

            runningCommand.add(identifier);
            args.setIdentifier(identifier);

            eventListeners.forEach((x, y) -> {
                if (runningCommand.contains(identifier))
                    y.accept(args);
            });

            if (runningCommand.contains(_identifier)) {
                Console.instance().writeLine("There is no command listener for the given command.", Console.PREFIX.COMMANDHANDLER, Console.PREFIX.INFO);
                runningCommand.remove(_identifier);
            }
        } else
            eventListeners.forEach((x, y) -> y.accept(args));
    }

    public void commandExecuted(Integer identifier) { runningCommand.remove(identifier); }
}
