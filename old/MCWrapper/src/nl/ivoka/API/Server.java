package nl.ivoka.API;

import nl.ivoka.Main;
import nl.ivoka.ServerManager;

import java.io.IOException;

public class Server {
    public static Server instance;

    public static class server {
        public server() {
            instance = new Server(Main.serverManager);
        }
    }

    private ServerManager serverManager;

    private Server(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public void broadcastMessage(String jsonMessage) throws IOException {
        this.runCommand("tellraw @a "+jsonMessage);
    }
    public void broadcastMessage(String message, String color) throws IOException {
        this.runCommand("tellraw @a {\"text\":\""+message+"\",\"color\":\""+color+"\"}");
    }

    public void runCommand(String command) {
        try {
            Main.serverManager.writeLine(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

