package nl.ivoka;

import nl.ivoka.API.Config;
import nl.ivoka.API.Console;
import nl.ivoka.EventArgs.*;
import nl.ivoka.EventArgs.PlayerEvents.*;
import nl.ivoka.EventArgs.ServerEvents.ServerOutputEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerSaveEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MinecraftConnector {

    ServerManager serverManager;
    public PlayerEvent events;

    public Map<String, String> players = new HashMap<>();

    /*
    private String ServerStart,
        ServerStop,
        ServerDone,
        ServerSaving,
        ServerSaved,
        ServerSaveOFF,
        ServerSaveON;
    */

    private String[] ServerStart,
            ServerStop,
            ServerDone,
            ServerSaving,
            ServerSaved,
            ServerSaveOFF,
            ServerSaveON;
    private String[] PlayerJoin,
        PlayerLeave,
        PlayerChat,
        PlayerPosition,
        PlayerUUID;

    public MinecraftConnector(ServerManager serverManager) throws DocumentException, IOException {
        loadEventValidators();

        this.serverManager = serverManager;
        serverManager.events.addListener((x) -> onDataReceived(x));

        events = new PlayerEvent();
    }

    public void onDataReceived(EventArgs x) {
        String data;
        if (x instanceof ServerOutputEventArgs)
            data = ((ServerOutputEventArgs) x).message;
        else
            return;

        if (data.length() < 12 || !data.contains(":"))
            return;

        String info = data.substring(11).trim();
        String message = info.substring(info.indexOf(":")+1).trim();

        // First check for chat and position (so players can't fake certain events)
        if (containsItemFromArray(info, PlayerChat)) {
            String[] split = message.split("[<>]", 3);
            String name = split[1];
            String chat = split[2].trim();

            events.broadcast(new PlayerChatEventArgs(name, chat));
        } else if (containsItemFromArray(info, PlayerPosition)) { // !message.contains("<") DO I NEED THIS?!
            String[] split = message.split("\\s*(\\]|,|\\s)\\s*");
            String name = split[2];

            if (isNumber(split[split.length - 3])) {
                String position = Math.round(Double.valueOf(split[split.length - 3])) + " " + Math.round(Double.valueOf(split[split.length - 2])) + " " + Math.round(Double.valueOf(split[split.length - 1]));

                events.broadcast(new PlayerPositionEventArgs(name, position));
            } else
                return;
        }

        // Second check for server events (saveing, stopping, done, etc.)
        else if (containsItemFromArray(info, ServerStart))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.START));
        else if (containsItemFromArray(info, ServerStop))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.STOP));
        else if (containsItemFromArray(info, ServerDone))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.DONE));

        else if (containsItemFromArray(info, ServerSaving))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVING));
        else if (containsItemFromArray(info, ServerSaved))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVED));
        else if (containsItemFromArray(info, ServerSaveOFF))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.OFF));
        else if (containsItemFromArray(info, ServerSaveON))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.ON));

        // Third check for other player events (joining, leaving, etc.)
        else if (containsItemFromArray(info, PlayerJoin)) {
            String name = message.split("\\s+")[0];

            events.broadcast(new PlayerJoinedEventArgs(name));
        } else if (containsItemFromArray(info, PlayerLeave)) {
            String name = message.split("\\s+")[0];
            players.remove(name);

            events.broadcast(new PlayerLeftEventArgs(name));
        }

        else if (containsItemFromArray(info, PlayerUUID)) {
            String[] split = message.split("\\s+");
            String name = split[split.length-3];
            String UUID = split[split.length-1];

            players.put(name, UUID);
        } else {
            return;
        }
    }

    boolean isNumber(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean containsItemFromArray(String inputString, String[] items) {
        return Arrays.stream(items).allMatch(inputString::contains);
    }

    public void printPlayersMapAsString() throws IOException {
        String content = players.entrySet()
                .stream()
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(", "));

        Console.instance.writeLine(content);
    }

    void loadEventValidators() throws DocumentException, IOException {
        Config eventValidator = new Config(new File(Main.configsDir+"/EventValidators.xml"));

        /*
        ServerStart = eventValidator.elementExistsAndNotEmpty("ServerStart") ? eventValidator.getValue("ServerStart"):"[Server thread/INFO]: Starting minecraft server";
        ServerStop = eventValidator.elementExistsAndNotEmpty("ServerStop") ? eventValidator.getValue("ServerStop"):"[Server Shutdown Thread/INFO]: Stopping server";
        ServerDone = eventValidator.elementExistsAndNotEmpty("ServerDone") ? eventValidator.getValue("ServerDone"):"[Server thread/INFO]: Done";

        ServerSaving = eventValidator.elementExistsAndNotEmpty("ServerSaving") ? eventValidator.getValue("ServerSaving"):"[Server thread/INFO]: Saving the game";
        ServerSaved = eventValidator.elementExistsAndNotEmpty("ServerSaved") ? eventValidator.getValue("ServerSaved"):"[Server thread/INFO]: Saved the game";
        ServerSaveOFF = eventValidator.elementExistsAndNotEmpty("ServerSaveOFF") ? eventValidator.getValue("ServerSaveOFF"):"[Server thread/INFO]: Automatic saving is now disabled";
        ServerSaveON = eventValidator.elementExistsAndNotEmpty("ServerSaveON") ? eventValidator.getValue("ServerSaveON"):"[Server thread/INFO]: Automatic saving is now enabled";
        */

        { // ServerStart
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerStart")) {
                for (String sEvent : eventValidator.getChildValues("ServerStart")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1)
                tmpServer.add("[Server thread/INFO]: Starting minecraft server");

            ServerStart = tmpServer.toArray(new String[0]);
        }
        { // ServerStop
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerStop")) {
                for (String sEvent : eventValidator.getChildValues("ServerStop")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1)
                tmpServer.add("[Server Shutdown Thread/INFO]: Stopping server");

            ServerStop = tmpServer.toArray(new String[0]);
        }
        { // ServerDone
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerDone")) {
                for (String sEvent : eventValidator.getChildValues("ServerDone")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1)
                tmpServer.add("[Server thread/INFO]: Done");

            ServerDone = tmpServer.toArray(new String[0]);
        }

        { // ServerSaving
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerSaving")) {
                for (String sEvent : eventValidator.getChildValues("ServerSaving")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1)
                tmpServer.add("[Server thread/INFO]: Saving the game");

            ServerSaving = tmpServer.toArray(new String[0]);
        }
        { // ServerSaved
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerSaved")) {
                for (String sEvent : eventValidator.getChildValues("ServerSaved")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1) {
                tmpServer.add("[Server thread/INFO]: ");
                tmpServer.add(" Saved the game");
            }

            ServerSaved = tmpServer.toArray(new String[0]);
        }
        { // ServerSaveOFF
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerSaveOFF")) {
                for (String sEvent : eventValidator.getChildValues("ServerSaveOFF")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1) {
                tmpServer.add("[Server thread/INFO]: ");
                tmpServer.add(" Automatic saving is now disabled");
            }

            ServerSaveOFF = tmpServer.toArray(new String[0]);
        }
        { // ServerSaveON
            List<String> tmpServer = new ArrayList<>();
            if (eventValidator.elementExists("ServerSaveON")) {
                for (String sEvent : eventValidator.getChildValues("ServerSaveON")) {
                    if (sEvent.length() > 0)
                        tmpServer.add(sEvent);
                }
            }
            if (tmpServer.size() < 1) {
                tmpServer.add("[Server thread/INFO]: ");
                tmpServer.add(" Automatic saving is now enabled");
            }

            ServerSaveON = tmpServer.toArray(new String[0]);
        }

        { // PlayerJoin
            List<String> tmpPlayer = new ArrayList<>();
            if (eventValidator.elementExists("PlayerJoin")) {
                for (String pEvent : eventValidator.getChildValues("PlayerJoin")) {
                    if (pEvent.length() > 0)
                        tmpPlayer.add(pEvent);
                }
            }
            if (tmpPlayer.size() < 1) {
                tmpPlayer.add("[Server thread/INFO]: ");
                tmpPlayer.add(" joined the game");
            }
            PlayerJoin = tmpPlayer.toArray(new String[0]);
        }
        { // PlayerLeave
            List<String> tmpPlayer = new ArrayList<>();
            if (eventValidator.elementExists("PlayerLeave")) {
                for (String pEvent : eventValidator.getChildValues("PlayerLeave")) {
                    if (pEvent.length() > 0)
                        tmpPlayer.add(pEvent);
                }
            }
            if (tmpPlayer.size() < 1) {
                tmpPlayer.add("[Server thread/INFO]: ");
                tmpPlayer.add(" left the game");
            }
            PlayerLeave = tmpPlayer.toArray(new String[0]);
        }
        { // PlayerChat
            List<String> tmpPlayer = new ArrayList<>();
            if (eventValidator.elementExists("PlayerChat")) {
                for (String pEvent : eventValidator.getChildValues("PlayerChat")) {
                    if (pEvent.length() > 0)
                        tmpPlayer.add(pEvent);
                }
            }
            if (tmpPlayer.size() < 1) {
                tmpPlayer.add("[Server thread/INFO]: ");
                tmpPlayer.add("<");
                tmpPlayer.add(">");
            }
            PlayerChat = tmpPlayer.toArray(new String[0]);
        }
        { // PlayerPosition
            List<String> tmpPlayer = new ArrayList<>();
            if (eventValidator.elementExists("PlayerPosition")) {
                for (String pEvent : eventValidator.getChildValues("PlayerPosition")) {
                    if (pEvent.length() > 0)
                        tmpPlayer.add(pEvent);
                }
            }
            if (tmpPlayer.size() < 1) {
                tmpPlayer.add("[Server thread/INFO]: ");
                tmpPlayer.add("Teleported");
            }
            PlayerPosition = tmpPlayer.toArray(new String[0]);
        }
        { // PlayerUUID
            List<String> tmpPlayer = new ArrayList<>();
            if (eventValidator.elementExists("PlayerUUID")) {
                for (String pEvent : eventValidator.getChildValues("PlayerUUID")) {
                    if (pEvent.length() > 0)
                        tmpPlayer.add(pEvent);
                }
            }
            if (tmpPlayer.size() < 1) {
                tmpPlayer.add("[User Authenticator #");
                tmpPlayer.add("/INFO]: UUID of player ");
            }
            PlayerUUID = tmpPlayer.toArray(new String[0]);
        }
    }
}
