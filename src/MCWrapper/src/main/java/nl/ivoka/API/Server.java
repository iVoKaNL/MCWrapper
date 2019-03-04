package nl.ivoka.API;

import nl.ivoka.Main;

public class Server {
    private static Server instance = null;

    public static synchronized Server instance() {
        if (instance == null)
            instance = new Server();
        return instance;
    }
    private Server() {}

    public void broadcastMessage(String jsonMessage) { this.runCommand("tellraw @a "+jsonMessage); }
    public void broadcastMessage(String message, String color) { this.runCommand("tellraw @a {\"text\":\""+message+"\",\"color\":\""+color+"\"}"); }

    public void runCommand(String command) { Main.getServerManager().writeLine(command); }
}
