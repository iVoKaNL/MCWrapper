package nl.ivoka.API;

import java.awt.*;
import java.io.IOException;

public class Console {
    public static Console instance;

    public static class console {
        public console() {
            instance = new Console();
        }
    }

    private Console() {
    }

    public void writeLine(String message) { write(message+"\n"); }
    public void write(String message) {
        try {
            System.out.print(message);
            Logger.instance.writeLog(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }
    public void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
}
