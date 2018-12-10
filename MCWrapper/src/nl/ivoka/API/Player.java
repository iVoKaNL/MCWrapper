package nl.ivoka.API;

public class Player {
    public static Player instance;

    public static class player {
        public player() {
            instance = new Player();
        }
    }

    private Player() {}

    public void sendMessageTo(String name, String jsonMessage) {
        Server.instance.runCommand("tellraw "+name+" "+jsonMessage);
    }
    public void sendMessageTo(String name, String message, String color) {
        Server.instance.runCommand("tellraw "+name+" {\"text\":\""+message+"\",\"color\":\""+color+"\"}");
    }

    public void refreshPosition(String name) {
        Server.instance.runCommand("tp "+name+" ~1 ~1 ~1");
    }
}
