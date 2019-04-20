package nl.ivoka;

import nl.ivoka.API.Console;
import nl.ivoka.API.Logger;
import nl.ivoka.API.MCWrapperXML;
import nl.ivoka.managers.plugin.PluginManager;
import nl.ivoka.managers.server.ServerManager;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MCWrapper {
    private static String name = "MCWrapper";

    private static ServerManager serverManager;
    private static PluginManager pluginManager;
    private static MCWrapperXML config;

    private static BufferedReader reader;
    private static Thread inputThread;

    private static File mcWrapperDir = new File(name);
    private static File pluginsDir;
    private static File configsDir;
    private static File mcWrapperConfig = new File(mcWrapperDir+"/"+name+".xml");

    public MCWrapper() {
        AnsiConsole.systemInstall();
        config = Main.getMCWrapperXML();
        reader = new BufferedReader(new InputStreamReader(System.in));

        if (config.isCustomWorkingDirectoryUsed())
            mcWrapperDir = new File(config.getCustomWorkingDirectory()+"/"+mcWrapperDir);
        pluginsDir = new File(mcWrapperDir+"/plugins");
        configsDir = new File(mcWrapperDir+"/configs");
        mcWrapperConfig = new File(mcWrapperDir+"/"+name+".xml");

        Console.instance();
        Logger.instance();
        Player.instance();
        Server.instance();

        // TODO make a option to show ConsoleColors.showColors() and an XML file to change colors


    }


    // region Setters

    // endregion

    // region Getters
    public static String getName() { return name; }

    public static File getMCWrapperDir() { return  mcWrapperDir; }
    public static File getConfigsDir() { return configsDir; }
    public static File getPluginsDir() { return pluginsDir; }

    public static File getMCWrapperConfig() { return mcWrapperConfig; }
    // endregion

    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.MCWRAPPER, Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.MCWRAPPER, Console.PREFIX.INFO); }
}
