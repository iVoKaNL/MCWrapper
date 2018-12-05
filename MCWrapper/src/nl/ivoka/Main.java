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
import org.xml.sax.SAXException;

import javax.xml.bind.PropertyException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Arrays;

public class Main {
    public static Config config;
    public static ServerManager serverManager;
    public static PluginManager pluginManager;

    private static BufferedReader reader;
    private static Thread inputThread;

    public static void main(String[] args) {
        try {
            config = new Config(new File("MCWrapper_3.xml"));

            config.saveConfig();

            System.out.println(Arrays.toString(config.getValues()));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        reader = new BufferedReader(new InputStreamReader(System.in));

        new Console.console();
        new Player.player();
        new Server.server();

        try {
            //config = new Config_old("MCWrapper.xml");
            config = new Config(new File("MCWrapper_2.xml"));
            config.setValue("ServerFile", "HOI");
            System.out.println(config.getValue("ServerFile"));

            //serverManager = new ServerManager(config.getValue("ServerFile"));
            serverManager = new ServerManager("minecraft.jar", "server");

            serverManager.start();

            //if (config.getValue("EnablePlugins") == "true")
            //   pluginManager = new PluginManager();
            if ("true" == "true")
                pluginManager = new PluginManager("server", serverManager);

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
        //} catch (ParserConfigurationException pcException) {

        //} catch (SAXException saxException) {
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
