package nl.ivoka.Plugin;

import nl.ivoka.API.Console;

public interface IMCWrapperPlugin {
    String getName();
    boolean isRunning();

    PluginManager getPluginManager();
    void setPluginManager(PluginManager pluginManager);

    default void initialize(PluginManager pluginManager) {
        if (pluginManager==null)
            Console.instance().writeLine("ERROR while loading "+getName()+"! PluginManager is NULL, report and add log file!!!");
        else
            setPluginManager(pluginManager);
    }
    void start();
    void reload();
    void stop();

    default void checkState() {
        if (isRunning() && !getPluginManager().getPluginState(getName()).equals(PluginManager.PLUGINSTATE.RUNNING))
            getPluginManager().changePluginState(getName(), PluginManager.PLUGINSTATE.RUNNING);
        else if (!isRunning() && !getPluginManager().getPluginState(getName()).equals(PluginManager.PLUGINSTATE.STOPPED))
            getPluginManager().changePluginState(getName(), PluginManager.PLUGINSTATE.STOPPED);
    }

    default void writeInfo(String line) { Console.instance().writeLine(line, Console.PREFIX.valueOf(getName().toUpperCase()), Console.PREFIX.INFO); }
    default void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.valueOf(getName().toUpperCase()), Console.PREFIX.ERROR); }
    default void writeError(String line) { Console.instance().writeLine(line, Console.PREFIX.valueOf(getName().toUpperCase()), Console.PREFIX.ERROR); }
}
