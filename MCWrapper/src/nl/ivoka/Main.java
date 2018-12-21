package nl.ivoka;

import nl.ivoka.API.*;
import nl.ivoka.API.Console;
import nl.ivoka.EventArgs.*;

import nl.ivoka.EventArgs.PlayerEvents.*;
import nl.ivoka.EventArgs.ServerEvents.ServerEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerSaveEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;
import nl.ivoka.Plugins.IMCWrapperPlugin;
import nl.ivoka.Plugins.PluginManager;
import org.dom4j.DocumentException;

import java.io.*;
import java.util.List;

public class Main {
    public static Config config;
    public static ServerManager serverManager;
    public static PluginManager pluginManager;

    private static BufferedReader reader;
    private static Thread inputThread;

    public static File pluginsDir;
    public static File configsDir;

    public static void main(String[] args) {
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));

            pluginsDir = new File("plugins/MCWrapper");
            configsDir = new File(pluginsDir+"/configs");

            if (!pluginsDir.exists())
                pluginsDir.mkdirs();
            if (!configsDir.exists())
                configsDir.mkdirs();

            new Console.console();
            new Player.player();
            new Server.server();
            new Logger.logger();

            //config = new Config(new File(configsDir+"/MCWrapper.xml"));
            config = new Config(new File("MCWrapper.xml"));

            // Check if WorkingDirectory attribute usecustom is set to true, if so get value and use that as working directory
            if (config.getAttribute("WorkingDirectory", "usecustom").equals("true")) {
                pluginsDir = new File(config.getValue("WorkingDirectory") + "/" + pluginsDir.getPath());
                configsDir = new File(config.getValue("WorkingDirectory") + "/" + configsDir.getPath());

                serverManager = new ServerManager(config.getValue("ServerFile"), new File(config.getValue("WorkingDirectory")));
            } else
                serverManager = new ServerManager(config.getValue("ServerFile"));

            serverManager.start();

            if (config.getValue("UsePlugins").equals("true"))
                pluginManager = new PluginManager(pluginsDir, serverManager);

            inputThread = new Thread(() -> inputThread());
            inputThread.start();

            serverManager.connector.events.addListener((x) -> {
                if (x instanceof PlayerChatEventArgs) {
                    PlayerChatEventArgs e = (PlayerChatEventArgs) x;
                    Console.instance.writeLine("PlayerChatEventArgs - " + e.name + " - " + e.chat);
                } else if (x instanceof PlayerJoinedEventArgs) {
                    PlayerJoinedEventArgs e = (PlayerJoinedEventArgs) x;
                    Console.instance.writeLine("PlayerJoinedEventArgs - " + e.name);
                } else if (x instanceof PlayerLeftEventArgs) {
                    PlayerLeftEventArgs e = (PlayerLeftEventArgs) x;
                    Console.instance.writeLine("PlayerLeftEventArgs - " + e.name);
                } else if (x instanceof PlayerPositionEventArgs) {
                    PlayerPositionEventArgs e = (PlayerPositionEventArgs) x;
                    Console.instance.writeLine("PlayerPositionEventArgs - " + e.name + " - " + e.position);
                } else if (x instanceof ServerStatusEventArgs) {
                    ServerStatusEventArgs e = (ServerStatusEventArgs) x;
                    Console.instance.writeLine("ServerStatusEventArgs - " + e.event.toString());

                    // IMPORTANT
                    try {
                        if (!serverManager.calledStop && e.event == ServerStatusEventArgs.Event.STOP)
                            serverManager.stop();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (x instanceof ServerSaveEventArgs) {
                    ServerSaveEventArgs e = (ServerSaveEventArgs) x;
                    Console.instance.writeLine("ServerSaveEventArgs - " + e.event.toString());
                } else if (x instanceof ServerEventArgs) {
                    Console.instance.writeLine("ServerEventArgs");
                } else if (x instanceof EventArgs) {
                    Console.instance.writeLine("EventArgs");
                } else
                    return;
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mcwrapperCommandHandler(String line) throws IOException {
        String command = line.substring(1, line.length()).split("\\s+")[0];
        String[] args = line.substring(command.length()+1, line.length()).trim().split("\\s+");

        if (args[0].isEmpty())
            args = new String[0];

        if (command.equals("plugins")) {
            if (args.length == 0) {
                Console.instance.writeLine("Usage:");
                Console.instance.writeLine("!plugins list - Show a list of all plugins");
                Console.instance.writeLine("!plugins reload [plugin] - Reloads all plugins, when [plugin] argument is given reload specific plugin");
                Console.instance.writeLine("!plugins stop [plugin] - Stops all plugins, when [plugin] argument is given stop specific plugin");
                Console.instance.writeLine("!plugins start [plugin] - Start all plugins, when [plugin] argument is given start specific plugin");
            } else if (args.length > 0) {
                if (args[0].equals("list")) {
                    String message = "Plugins("+pluginManager.plugins.size()+"): ";

                    for (IMCWrapperPlugin plugin : pluginManager.plugins.keySet()) {
                        message+=plugin.Name()+", ";
                    }
                    message = message.substring(0, message.length()-2);

                    Console.instance.writeLine(message);
                } else if (args[0].equals("reload")) {
                    if (args.length == 1)
                        pluginManager.reloadPlugins();
                    else if (args.length > 1)
                        pluginManager.reloadPlugin(args[1]);
                } else if (args[0].equals("stop")) {
                    if (args.length == 1)
                        pluginManager.stopPlugins();
                    else if (args.length > 1)
                        pluginManager.stopPlugin(args[1]);
                } else if (args[0].equals("start")) {
                    if (args.length == 1)
                        pluginManager.startPlugins();
                    else if (args.length > 1)
                        pluginManager.startPlugin(args[1]);
                }
            } else {
                Console.instance.writeLine("Error while executing '"+line+"'.");
            }
        }
    }

    public static void inputThread() {
        try {
            for (String line = readLine(); line != null; line = readLine()) {
                if (line.equals("stop")) {
                    serverManager.stop();
                    break;
                } else if (line.toCharArray().length > 1) {
                    if (line.toCharArray()[0] == '!')
                        mcwrapperCommandHandler(line);
                    else
                        serverManager.writeLine(line);
                } else {
                    serverManager.writeLine(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String readLine() throws IOException { return reader.readLine().trim(); }
}
