package nl.ivoka;

import nl.ivoka.EventArgs.*;
import nl.ivoka.EventArgs.PlayerEvents.*;
import nl.ivoka.EventArgs.ServerEvents.ServerOutputEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerSaveEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MinecraftConnector {

    ServerManager serverManager;
    public PlayerEvent events;

    public Map<String, String> players = new HashMap<>();

    public MinecraftConnector(ServerManager serverManager) {
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

        if (data.contains("[Server thread/INFO]: Starting minecraft server"))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.START));
        else if (data.contains("[Server Shutdown Thread/INFO]: Stopping server"))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.STOP));
        else if (data.contains("[Server thread/INFO]: Done"))
            events.broadcast(new ServerStatusEventArgs(ServerStatusEventArgs.Event.DONE));

        else if (data.contains("[Server thread/INFO]: Saving the game"))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVING));
        else if (data.contains("[Server thread/INFO]: Saved the game"))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.SAVED));
        else if (data.contains("[Server thread/INFO]: Automatic saving is now disabled"))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.OFF));
        else if (data.contains("[Server thread/INFO]: Automatic saving is now enabled"))
            events.broadcast(new ServerSaveEventArgs(ServerSaveEventArgs.Event.ON));

        else if (message.contains("joined the game")) {
            String name = message.split("\\s+")[0];

            events.broadcast(new PlayerJoinedEventArgs(name));
        } else if (message.contains("left the game")) {
            String name = message.split("\\s+")[0];
            players.remove(name);

            events.broadcast(new PlayerLeftEventArgs(name));
        }

        else if (message.contains("<") && message.contains(">")) {
            String[] split = message.split("[<>]", 3);
            String name = split[1];
            String chat = split[2].trim();

            events.broadcast(new PlayerChatEventArgs(name, chat));
        } else if (!message.contains("<") && message.contains("Teleported")) {
            String[] split = message.split("\\s*(\\]|,|\\s)\\s*");
            String name = split[2];

            if (isNumber(split[split.length - 3])) {
                String position = Math.round(Double.valueOf(split[split.length - 3])) + " " + Math.round(Double.valueOf(split[split.length - 2])) + " " + Math.round(Double.valueOf(split[split.length - 1]));

                events.broadcast(new PlayerPositionEventArgs(name, position));
            } else
                return;
        } else if (info.contains("[User Authenticator #") && info.contains("/INFO]: UUID of player ")) {
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

    public void printPlayersMapAsString() {
        String content = players.entrySet()
                .stream()
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(", "));

        System.out.println(content);
    }
}
