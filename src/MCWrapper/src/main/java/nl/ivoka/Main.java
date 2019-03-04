package nl.ivoka;

import nl.ivoka.API.*;
import nl.ivoka.Events.EventArgs;
import nl.ivoka.Events.Handlers.CommandHandler;
import nl.ivoka.Events.Handlers.EventHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Events.PlayerEvents.*;
import nl.ivoka.Events.ServerEvents.*;
import nl.ivoka.Plugin.PluginManager;
import nl.ivoka.Server.ServerManager;
import org.dom4j.DocumentException;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static String name = "MCWrapper";

    private static Config config;
    private static ServerManager serverManager;
    private static PluginManager pluginManager;

    private static BufferedReader reader;
    private static Thread inputThread;

    private static File pluginsDir;
    private static File configsDir;

    private static boolean debug=false;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        try {
            boolean customWD = false;

            reader = new BufferedReader(new InputStreamReader(System.in));

            pluginsDir = new File("plugins/MCWrapper");
            configsDir = new File(pluginsDir + "/configs");

            config = new Config(new File("MCWrapper.xml"));
            debug = Boolean.valueOf(config.getValue("Debug"));

            Console.instance();
            Logger.instance();
            Player.instance();
            Server.instance();

            //ConsoleColors.showColors();

            debug("Initialized Console-, Logger-, Player-, Server- and default Config instances.");
            debug("Checking if 'WorkingDirectory' attribute 'usecustom' is set to true.\n" +
                    "If set to true retrieve value and use it as working directory.\n" +
                    "Also change default Config location.\n" +
                    "Note: when using custom WorkingDirectory the default config will be reloaded and thus the debug value in the new Config file will be used.");

            if (config.getAttribute("WorkingDirectory", "usecustom").equals("true")) {
                customWD = true;

                debug("Using custom working directory.");
                debug("Changing pluginsDir, configsDir and change default Config location.");

                String workingDirectory = config.getValue("WorkingDirectory");
                pluginsDir = new File(workingDirectory + "/" + pluginsDir.getPath());
                configsDir = new File(workingDirectory + "/" + configsDir.getPath());
            } else
                debug("Using default working directory.");

            createDirs();

            config = new Config(new File(configsDir + "/MCWrapper.xml"));
            debug = Boolean.valueOf(config.getValue("Debug"));

            debug("Starting server manager...");
            if (customWD)
                serverManager = new ServerManager(config.getValue("ServerFile"), new File(config.getValue("WorkingDirectory")));
            else
                serverManager = new ServerManager(config.getValue("ServerFile"));

            serverManager.start();
            debug("Started server manager.");

            debug("Checking if plugins will be used...");
            if (config.getValue("UsePlugins").equals("true")) {
                debug("Plugins will be used.");
                debug("Starting plugin manager....");
                pluginManager = new PluginManager(pluginsDir);
                debug("Started plugin manager...");
            } else
                debug("Plugins won't be used.");

            inputThread = new Thread(Main::inputThread);
            inputThread.start();

            if (config.getValue("DebugEvents").equals("true"))
                EventHandler.instance().addListener(getName(), Main::eventListener);

            EventHandler.instance().addListener(getName() + "-serverStopListener", Main::stopListener);

        } catch (IOException | DocumentException e) {
            Console.instance().writeLine(e, Console.PREFIX.ERROR, Console.PREFIX.MAIN);
        }
    }

    private static void stopListener(EventArgs x) {
        if (x instanceof ServerStatusEvent) {
            ServerStatusEvent e = (ServerStatusEvent)x;

            if (e.event.equals(ServerStatusEvent.Event.STOP))
                serverManager.stop();
        }
    }

    private static void eventListener(EventArgs x) {
        if (x instanceof PlayerChatEvent) {
            PlayerChatEvent e = (PlayerChatEvent) x;
            debug("PlayerChatEvent - " + e.name + " - " + e.chat, true);
        } else if (x instanceof PlayerJoinedEvent) {
            PlayerJoinedEvent e = (PlayerJoinedEvent) x;
            debug("PlayerJoinedEvent - " + e.name, true);
        } else if (x instanceof PlayerLeftEvent) {
            PlayerLeftEvent e = (PlayerLeftEvent) x;
            debug("PlayerLeftEvent - " + e.name, true);
        } else if (x instanceof PlayerPositionEvent) {
            PlayerPositionEvent e = (PlayerPositionEvent) x;
            debug("PlayerPositionEvent - " + e.name + " - " + e.position, true);
        } else if (x instanceof ServerStatusEvent) {
            ServerStatusEvent e = (ServerStatusEvent) x;
            debug("ServerStatusEvent - " + e.event.toString(), true);

            // IMPORTANT
            if (!serverManager.getCalledStop() && e.event == ServerStatusEvent.Event.STOP)
                serverManager.stop();
        } else if (x instanceof ServerSaveEvent) {
            ServerSaveEvent e = (ServerSaveEvent) x;
            debug("ServerSaveEventArgs - " + e.event.toString(), true);
        } else if (x instanceof ServerEvent) {
            debug("ServerEventArgs", true);
        } else if (x != null) {
            debug("EventArgs", true);
        }
    }

    // TODO add !config reload [config] (to reload variables like debug and debugevents)
    private static void inputThread() {
        for (String line=readLine(); !serverManager.getCalledStop(); line=readLine()) {
            if (line.length()>0) {
                if (line.equals("stop")) {
                    serverManager.stop();
                    break;
                } else if (line.toCharArray()[0] == '!') {
                    CommandEvent event = new CommandEvent(line);
                    if (event.command.equals("plugins"))
                        pluginCommandListener(event);
                    else if (event.command.equals("help")) {
                        new nl.ivoka.Commands.plugins(null).help();
                        CommandHandler.instance().fireEvent(event, true);
                    } else
                        CommandHandler.instance().fireEvent(event);
                } else
                    serverManager.writeLine(line);
            }
        }
    }

    public static void debug(String msg) { debug(msg, false); }
    public static void debug(String msg, boolean override) {
        if (debug||override)
            Console.instance().writeLine(msg, Console.PREFIX.MAIN, Console.PREFIX.DEBUG);
    }

    public static String readLine() {
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            Console.instance().writeLine(e, Console.PREFIX.ERROR, Console.PREFIX.MAIN);
        }
        return "error";
    }

    private static void createDirs() {
        debug("Creating directories if not exist...");
        if (!pluginsDir.exists())
            pluginsDir.mkdirs();
        if (!configsDir.exists())
            configsDir.mkdirs();
    }

    private static void pluginCommandListener(EventArgs x) { new nl.ivoka.Commands.plugins((CommandEvent)x); }

    @Deprecated
    public static void stopInputThread() { inputThread.stop(); }

    public static ServerManager getServerManager() { return serverManager; }
    public static PluginManager getPluginManager() { return pluginManager; }

    public static String getName() { return name; }
    public static Config getConfig() { return config; }

    public static File getPluginsDir() { return pluginsDir; }
    public static File getConfigsDir() { return configsDir; }

    public static boolean getDebug() { return debug; }
}
