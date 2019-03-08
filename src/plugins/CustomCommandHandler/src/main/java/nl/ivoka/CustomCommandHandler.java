package nl.ivoka;

import nl.ivoka.API.Commands.CommandListener;
import nl.ivoka.Events.EventArgs;
import nl.ivoka.Events.Handlers.CommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Plugin.IMCWrapperPlugin;
import nl.ivoka.Plugin.PluginManager;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@MetaInfServices
public class CustomCommandHandler implements IMCWrapperPlugin, CommandListener {
    private static String name = "CustomCommandHandler";
    private static boolean running = false;

    private PluginManager pluginManager;

    public CustomCommandHandler() {}

    @Override
    public void CommandListener(EventArgs x) {
        CommandEvent e = (CommandEvent)x;

        try {
            Class<?> eventClass = Class.forName("nl.ivoka.CustomCommands."+e.command);
            Constructor<?> eventConstructor = eventClass.getConstructor(CommandEvent.class, CustomCommandHandler.class);
            eventConstructor.newInstance(e, this);

            if (e.identifier!=null)
                CommandHandler.instance().commandExecuted(e.identifier);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {}
    }

    public String getName() { return name; }
    public boolean isRunning() { return running; }

    public PluginManager getPluginManager() { return pluginManager; }
    public void setPluginManager(PluginManager pluginManager) { this.pluginManager=pluginManager; }

    @Override
    public void initialize(PluginManager pluginManager) {
        if (pluginManager==null)
            writeError(name+" pluginManager variable is NULL!!! Shouldn't be possible, please report! Add log file!");
        else
            this.pluginManager=pluginManager;
    }

    public void start() {
        if (running)
            writeError(name+" is already running!");
        else if (pluginManager==null)
            writeError(name+" pluginManager variable is NULL!!! Shouldn't be possible, please report! Add log file!");
        else {
            running=true;
            CommandHandler.instance().addListener(name, this::CommandListener);
        }

        checkState();
    }
    public void reload() {
        if (!running)
            writeError(name+" is not running! You can use start instead of reload.");
        else {
            stop();
            start();
        }
    }
    public void stop() {
        if (!running)
            writeError(name+" is not running!");
        else {
            CommandHandler.instance().removeListener(name);
            running=false;
        }

        checkState();
    }

    @Override
    public void checkState() {
        if (running && !pluginManager.getPluginState(name).equals(PluginManager.PLUGINSTATE.RUNNING))
            pluginManager.changePluginState(name, PluginManager.PLUGINSTATE.RUNNING);
        else if (!running && !pluginManager.getPluginState(name).equals(PluginManager.PLUGINSTATE.STOPPED))
            pluginManager.changePluginState(name, PluginManager.PLUGINSTATE.STOPPED);
    }
}
