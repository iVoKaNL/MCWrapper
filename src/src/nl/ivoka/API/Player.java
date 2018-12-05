package nl.ivoka.API;

import java.io.IOException;

public class Player {
    public static Player instance;

    public static class player {
        public player() {
            instance = new Player();
        }
    }

    private Player() {}

    public void sendMessageTo(String name, String jsonMessage) throws IOException {
        Server.instance.runCommand("tellraw "+name+" "+jsonMessage);
    }
    public void sendMessageTo(String name, String message, String color) throws IOException {
        Server.instance.runCommand("tellraw "+name+" {\"text\":\""+message+"\",\"color\":\""+color+"\"}");
    }

    public void refreshPosition(String name) throws IOException {
        Server.instance.runCommand("tp "+name+" ~ ~ ~");
    }
}
