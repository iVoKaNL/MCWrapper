package nl.ivoka;

import nl.ivoka.API.*;
import nl.ivoka.API.Console;
import nl.ivoka.EventArgs.*;

import nl.ivoka.EventArgs.PlayerEvents.*;
import nl.ivoka.EventArgs.ServerEvents.ServerEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerSaveEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;
import nl.ivoka.Plugins.PluginManager;
import org.dom4j.DocumentException;

import java.io.*;

public class Main {
    public static Config config;
    public static ServerManager serverManager;
    public static PluginManager pluginManager;

    private static BufferedReader reader;
    private static Thread inputThread;

    public static File pluginsDir;
    public static File configsDir;

    public static void main(String[] args) {
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

        try {
            //config = new Config(new File(configsDir+"/MCWrapper.xml"));
            config = new Config(new File("MCWrapper.xml"));

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
                    System.out.println("PlayerChatEventArgs - " + e.name + " - " + e.chat);
                } else if (x instanceof PlayerJoinedEventArgs) {
                    PlayerJoinedEventArgs e = (PlayerJoinedEventArgs) x;
                    System.out.println("PlayerJoinedEventArgs - " + e.name);
                } else if (x instanceof PlayerLeftEventArgs) {
                    PlayerLeftEventArgs e = (PlayerLeftEventArgs) x;
                    System.out.println("PlayerLeftEventArgs - " + e.name);
                } else if (x instanceof PlayerPositionEventArgs) {
                    PlayerPositionEventArgs e = (PlayerPositionEventArgs) x;
                    System.out.println("PlayerPositionEventArgs - " + e.name + " - " + e.position);
                } else if (x instanceof ServerStatusEventArgs) {
                    ServerStatusEventArgs e = (ServerStatusEventArgs) x;
                    System.out.println("ServerStatusEventArgs - " + e.event.toString());
                } else if (x instanceof ServerSaveEventArgs) {
                    ServerSaveEventArgs e = (ServerSaveEventArgs) x;
                    System.out.println("ServerSaveEventArgs - " + e.event.toString());
                } else if (x instanceof ServerEventArgs) {
                    System.out.println("ServerEventArgs");
                } else if (x instanceof EventArgs) {
                    System.out.println("EventArgs");
                } else
                    return;
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void inputThread() {
        try {
            for (String line = readLine(); line != null; line = readLine()) {
                if (line.equals("stop")) {
                    serverManager.stop();
                    break;
                }
                serverManager.writeLine(line);
            }
        } catch (IOException ioException) {

        } catch (InterruptedException iException) {

        }
    }

    public static String readLine() throws IOException { return reader.readLine().trim(); }
}
