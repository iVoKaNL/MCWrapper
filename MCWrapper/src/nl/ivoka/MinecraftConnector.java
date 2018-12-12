package nl.ivoka;

import nl.ivoka.API.Config;
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

    private String ServerStart,
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

    void loadEventValidators() throws DocumentException, IOException {
        Config eventValidator = new Config(new File(Main.configsDir+"/EventValidators.xml"));

        ServerStart = eventValidator.elementExistsAndNotEmpty("ServerStart") ? eventValidator.getValue("ServerStart"):"[Server thread/INFO]: Starting minecraft server";
        ServerStop = eventValidator.elementExistsAndNotEmpty("ServerStop") ? eventValidator.getValue("ServerStop"):"[Server Shutdown Thread/INFO]: Stopping server";
        ServerDone = eventValidator.elementExistsAndNotEmpty("ServerDone") ? eventValidator.getValue("ServerDone"):"[Server thread/INFO]: Done";

        ServerSaving = eventValidator.elementExistsAndNotEmpty("ServerSaving") ? eventValidator.getValue("ServerSaving"):"[Server thread/INFO]: Saving the game";
        ServerSaved = eventValidator.elementExistsAndNotEmpty("ServerSaved") ? eventValidator.getValue("ServerSaved"):"[Server thread/INFO]: Saved the game";
        ServerSaveOFF = eventValidator.elementExistsAndNotEmpty("ServerSaveOFF") ? eventValidator.getValue("ServerSaveOFF"):"[Server thread/INFO]: Automatic saving is now disabled";
        ServerSaveON = eventValidator.elementExistsAndNotEmpty("ServerSaveON") ? eventValidator.getValue("ServerSaveON"):"[Server thread/INFO]: Automatic saving is now enabled";

        { // PlayerJoin
            List<String> _PlayerJoin = new ArrayList<>();
            if (eventValidator.elementExists("PlayerJoin")) {
                for (String pJoin : eventValidator.getChildValues("PlayerJoin")) {
                    if (pJoin.length() > 0)
                        _PlayerJoin.add(pJoin);
                }
            }
            if (_PlayerJoin.size() < 1) {
                _PlayerJoin.add("[Server thread/INFO]: ");
                _PlayerJoin.add(" joined the game");
            }
            PlayerJoin = _PlayerJoin.toArray(new String[0]);
        }
        { // PlayerLeave
            List<String> _PlayerLeave = new ArrayList<>();
            if (eventValidator.elementExists("PlayerLeave")) {
                for (String pLeave : eventValidator.getChildValues("PlayerLeave")) {
                    if (pLeave.length() > 0)
                        _PlayerLeave.add(pLeave);
                }
            }
            if (_PlayerLeave.size() < 1) {
                _PlayerLeave.add("[Server thread/INFO]: ");
                _PlayerLeave.add(" left the game");
            }
            PlayerLeave = _PlayerLeave.toArray(new String[0]);
        }
        { // PlayerChat
            List<String> _PlayerChat = new ArrayList<>();
            if (eventValidator.elementExists("PlayerChat")) {
                for (String pChat : eventValidator.getChildValues("PlayerChat")) {
                    if (pChat.length() > 0)
                        _PlayerChat.add(pChat);
                }
            }
            if (_PlayerChat.size() < 1) {
                _PlayerChat.add("[Server thread/INFO]: ");
                _PlayerChat.add("<");
                _PlayerChat.add(">");
            }
            PlayerChat = _PlayerChat.toArray(new String[0]);
        }
        { // PlayerPosition
            List<String> _PlayerPosition = new ArrayList<>();
            if (eventValidator.elementExists("PlayerPosition")) {
                for (String pPosition : eventValidator.getChildValues("PlayerPosition")) {
                    if (pPosition.length() > 0)
                        _PlayerPosition.add(pPosition);
                }
            }
            if (_PlayerPosition.size() < 1) {
                _PlayerPosition.add("[Server thread/INFO]: ");
                _PlayerPosition.add("Teleported");
            }
            PlayerPosition = _PlayerPosition.toArray(new String[0]);
        }
        { // PlayerUUID
            List<String> _PlayerUUID = new ArrayList<>();
            if (eventValidator.elementExists("PlayerUUID")) {
                for (String pUUID : eventValidator.getChildValues("PlayerUUID")) {
                    if (pUUID.length() > 0)
                        _PlayerUUID.add(pUUID);
                }
            }
            if (_PlayerUUID.size() < 1) {
                _PlayerUUID.add("[User Authenticator #");
                _PlayerUUID.add("/INFO]: UUID of player ");
            }
            PlayerUUID = _PlayerUUID.toArray(new String[0]);
        }
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

        if (info.contains(ServerStart))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.START));
        else if (info.contains(ServerStop))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.STOP));
        else if (info.contains(ServerDone))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.DONE));

        else if (info.contains(ServerSaving))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVING));
        else if (info.contains(ServerSaved))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVED));
        else if (info.contains(ServerSaveOFF))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.OFF));
        else if (info.contains(ServerSaveON))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.ON));

        else if (containsItemFromArray(info, PlayerChat)) {
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

    public void printPlayersMapAsString() {
        String content = players.entrySet()
                .stream()
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(", "));

        System.out.println(content);
    }
}
