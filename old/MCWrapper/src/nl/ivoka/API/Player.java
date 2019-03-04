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

    //public void refreshPosition(String name) { Server.instance.runCommand("tp "+name+" ~ ~ ~"); }
    public void refreshPosition(String name) { runCommandAsPlayer(name, "tp ~ ~ ~"); }

    public void runCommandAsPlayer(String name, String command) { Server.instance.runCommand("execute as @p[name="+name+"] run "+command); }
}