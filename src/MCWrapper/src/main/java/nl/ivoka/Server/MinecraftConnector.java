package nl.ivoka.Server;

import nl.ivoka.API.Config;
import nl.ivoka.API.Console;
import nl.ivoka.Events.EventArgs;
import nl.ivoka.Events.Handlers.EventHandler;
import nl.ivoka.Events.PlayerEvents.*;
import nl.ivoka.Events.ServerEvents.*;
import nl.ivoka.Main;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MinecraftConnector {
    public Map<String, String> players = new HashMap<>();

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

    public void onDataReceived(EventArgs e) {
        String data;

        if (e instanceof ServerOutputEvent)
            data = ((ServerOutputEvent)e).message;
        else return;

        if (data.length() < 12 || !data.contains(":"))
            return;

        String info = data.substring(11).trim();
        String message = info.substring(info.indexOf(":")+1).trim();

        // First check for chat and position (so players can't fake certain events)
        if (containsItemFromArray(info, PlayerChat)) {
            String[] split = message.split("[<>]", 3);
            String name = split[1];
            String chat = split[2].trim();

            EventHandler.instance().fireEvent(new PlayerChatEvent(name, chat));
        } else if (containsItemFromArray(info, PlayerPosition)) { // !message.contains("<") DO I NEED THIS?!
            String[] split = message.split("\\s*(\\]|,|\\s)\\s*");
            String name = split[2];

            if (isNumber(split[split.length - 3])) {
                String position = Math.round(Double.valueOf(split[split.length - 3])) + " " + Math.round(Double.valueOf(split[split.length - 2])) + " " + Math.round(Double.valueOf(split[split.length - 1]));

                EventHandler.instance().fireEvent(new PlayerPositionEvent(name, position));
            } else
                return;
        }

        // Second check for server events (saveing, stopping, done, etc.)
        else if (containsItemFromArray(info, ServerStart))
            EventHandler.instance().fireEvent(new ServerStatusEvent(ServerStatusEvent.Event.START));
        else if (containsItemFromArray(info, ServerStop))
            EventHandler.instance().fireEvent(new ServerStatusEvent(ServerStatusEvent.Event.STOP));
        else if (containsItemFromArray(info, ServerDone))
            EventHandler.instance().fireEvent(new ServerStatusEvent(ServerStatusEvent.Event.DONE));

        else if (containsItemFromArray(info, ServerSaving))
            EventHandler.instance().fireEvent(new ServerSaveEvent(ServerSaveEvent.Event.SAVING));
        else if (containsItemFromArray(info, ServerSaved))
            EventHandler.instance().fireEvent(new ServerSaveEvent(ServerSaveEvent.Event.SAVED));
        else if (containsItemFromArray(info, ServerSaveOFF))
            EventHandler.instance().fireEvent(new ServerSaveEvent(ServerSaveEvent.Event.OFF));
        else if (containsItemFromArray(info, ServerSaveON))
            EventHandler.instance().fireEvent(new ServerSaveEvent(ServerSaveEvent.Event.ON));

        // Third check for other player events (joining, leaving, etc.)
        else if (containsItemFromArray(info, PlayerJoin)) {
            String name = message.split("\\s+")[0];

            EventHandler.instance().fireEvent(new PlayerJoinedEvent(name));
        } else if (containsItemFromArray(info, PlayerLeave)) {
            String name = message.split("\\s+")[0];
            players.remove(name);

            EventHandler.instance().fireEvent(new PlayerLeftEvent(name));
        }

        else if (containsItemFromArray(info, PlayerUUID)) {
            String[] split = message.split("\\s+");
            String name = split[split.length-3];
            String UUID = split[split.length-1];

            players.put(name, UUID);
        }
    }

    private boolean isNumber(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
        /*
            In the matches() method,

            1. -? allows zero or more - for negative numbers in the string.
            2. \\d+ checks the string must have atleast 1 or more numbers (\\d).
            3. (\\.\\d+)? allows zero or more of the given pattern (\\.\\d+) in which
                3.a \\. checks if the string contains . (decimal points) or not
                3.b If yes, it should be followed by at least one or more number \\d+.
         */
    }
    public void printPlayersMapAsString() {
        String content = players.entrySet()
                .stream()
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(", "));

        writeInfo(content);
    }
    private static boolean containsItemFromArray(String inputString, String[] items) { return Arrays.stream(items).allMatch(inputString::contains); }

    // TODO maybe add <code>Main.debug('loaded default validator for [event]');</code>
    public MinecraftConnector() {
        try {
            Config eventValidator = new Config(new File(Main.getConfigsDir()+"/EventValidators.xml"));

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

            writeInfo("Loaded all EventValidators");
        } catch (IOException | DocumentException e) {
            writeError(e);
        }
    }

    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.MINECRAFTCONNECTOR, Console.PREFIX.INFO); }
    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.MINECRAFTCONNECTOR, Console.PREFIX.ERROR); }
}
