package nl.ivoka;

import nl.ivoka.API.console.Console;
import nl.ivoka.API.console.ConsoleColors;
import nl.ivoka.API.console.Logger;
import nl.ivoka.API.server.Player;
import nl.ivoka.API.server.Server;
import nl.ivoka.API.xml.MCWrapperXML;
import nl.ivoka.events.EventArgs;
import nl.ivoka.events.handler.EventHandler;
import nl.ivoka.events.server.ServerOutputEventArgs;
import nl.ivoka.managers.plugin.PluginManager;
import nl.ivoka.managers.server.ServerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MCWrapper {
    private final static String name = "MCWrapper";
    private final static String stopListener = name+"-serverStopListener";

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
        config = Main.getMCWrapperXML();
        reader = new BufferedReader(new InputStreamReader(System.in));

        if (config.isCustomWorkingDirectoryUsed())
            mcWrapperDir = new File(config.getCustomWorkingDirectory()+"/"+mcWrapperDir);
        pluginsDir = new File(mcWrapperDir+"/plugins");
        configsDir = new File(mcWrapperDir+"/configs");
        mcWrapperConfig = new File(mcWrapperDir+"/"+name+".xml");

        // TODO make a option to show ConsoleColors.showColors() and an XML file to change colors

        initializeAPIs(); createRequiredDirectories();

        serverManager = new ServerManager();
        serverManager.start();

        if (config.isPluginsUsed())
            pluginManager = new PluginManager();

        inputThread = new Thread(MCWrapper::inputThread);
        inputThread.start();

        EventHandler.instance().addListener("TEMP", MCWrapper::temp);
        // TODO add serverstoplistener
    }

    public static void temp(EventArgs eventArgs) {
        if (!(eventArgs instanceof ServerOutputEventArgs)) {
            System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT + eventArgs.toString() + ConsoleColors.RESET);
        }
    }

                            // region Misc
    private void initializeAPIs() {
        Console.instance();
        Logger.instance();
        Player.instance();
        Server.instance();

        Logger.instance().reload();
    }

    private void createRequiredDirectories() {
        if (!pluginsDir.exists())
            pluginsDir.mkdirs();
        if (!configsDir.exists())
            configsDir.mkdirs();
    }

    private static void inputThread() {
        for (String line=readLine(); !serverManager.isStopping(); line=readLine()) {
            if (line.length()>0) {
                if (line.equals("stop")) {
                    serverManager.stop();
                    break;
                } else if (line.toCharArray()[0] == '!') {
                    // TODO Add commandhandler fireevent
                } else
                    serverManager.writeLine(line);
            }
        }
        // TODO check why pluginManager c1 is created in previous version
    }

    public static String readLine() {
        try { return reader.readLine().trim(); }
        catch (IOException e) { writeError(e); }
        return "error";
    }
    // endregion
    // region Getters
    public static String getName() { return name; }
    public static String getStopListener() { return stopListener; }

    public static File getMCWrapperDir() { return  mcWrapperDir; }
    public static File getConfigsDir() { return configsDir; }
    public static File getPluginsDir() { return pluginsDir; }

    public static File getMCWrapperConfig() { return mcWrapperConfig; }

    public static ServerManager getServerManager() { return serverManager; }
    // endregion

    private static void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.MCWRAPPER, Console.PREFIX.ERROR); }
    private static void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.MCWRAPPER, Console.PREFIX.INFO); }
}
