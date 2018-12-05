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

    public void writeLine(String message) {
        System.out.println(message);
    }
    public void write(String message) {
        System.out.print(message);
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
