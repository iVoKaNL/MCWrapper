package nl.ivoka.API.Commands;

import nl.ivoka.Events.EventArgs;
import nl.ivoka.Events.Handlers.CommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface CommandListener {
    default void CommandListener(EventArgs x) {
        CommandEvent e = (CommandEvent)x;

        try {
            Class<?> eventClass = Class.forName("nl.ivoka.Commands."+e.command);
            Constructor<?> eventConstructor = eventClass.getConstructor(CommandEvent.class);
            eventConstructor.newInstance(e);

            if (x.identifier!=null)
                CommandHandler.instance().commandExecuted(x.identifier);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {}
    }
}
